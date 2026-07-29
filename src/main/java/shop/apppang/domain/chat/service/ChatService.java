package shop.apppang.domain.chat.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import shop.apppang.domain.chat.client.GeminiClient;
import shop.apppang.domain.chat.dto.ChatHistoryResponse;
import shop.apppang.domain.chat.dto.ChatRequest;
import shop.apppang.domain.chat.dto.ChatResponse;
import shop.apppang.domain.chat.dto.ChatResponse.RecommendedProduct;
import shop.apppang.domain.chat.entity.ChatMessageEntity;
import shop.apppang.domain.chat.repository.ChatMessageRepository;
import shop.apppang.domain.product.entity.ProductEntity;
import shop.apppang.domain.product.entity.ProductImageEntity;
import shop.apppang.domain.product.repository.ProductImageRepository;
import shop.apppang.domain.product.repository.ProductRepository;
import shop.apppang.domain.user.entity.User;
import shop.apppang.domain.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final GeminiClient geminiClient;
    private final ChatMessageRepository chatMessageRepository;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public ChatResponse chat(Long userId, ChatRequest request) {
        // 1. 세션 ID (없으면 새로 발급)
        String sessionId = (request.sessionId() != null && !request.sessionId().isBlank())
                ? request.sessionId()
                : UUID.randomUUID().toString();

        // 2. 상품 목록 → 시스템 프롬프트
        String systemPrompt = buildSystemPrompt();

        // 3. 대화 턴 구성 (로그인 사용자는 이전 대화 이어가기)
        List<Map<String, Object>> contents = new ArrayList<>();
        if (userId != null) {
            List<ChatMessageEntity> history =
                    chatMessageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId);
            for (ChatMessageEntity m : history) {
                contents.add(turn(m.getRole(), m.getMessage()));
            }
        }
        contents.add(turn("USER", request.message()));

        // 4. Gemini 호출 (JSON 문자열: { "message": "...", "productIds": [..] })
        String rawReply = geminiClient.generate(systemPrompt, contents);

        // 5. Gemini JSON 파싱 → 사용자에게 보여줄 메시지 + 추천 상품 id
        GeminiReply parsed = parseGeminiReply(rawReply);
        String reply = parsed.message();

        // 6. 추천 상품 id로 상품 카드 조립 (실존 상품만, Gemini가 준 순서 유지)
        List<RecommendedProduct> recommendedProducts = buildRecommendedProducts(parsed.productIds());

        // 7. 로그인 사용자만 대화 저장 (원본 JSON이 아니라 사람이 읽을 메시지 텍스트만)
        if (userId != null) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "사용자를 찾을 수 없습니다."));
            chatMessageRepository.save(ChatMessageEntity.builder()
                    .user(user).sessionId(sessionId).role("USER").message(request.message()).build());
            chatMessageRepository.save(ChatMessageEntity.builder()
                    .user(user).sessionId(sessionId).role("MODEL").message(reply).build());
        }

        return new ChatResponse(reply, sessionId, recommendedProducts);
    }

    @Transactional(readOnly = true)
    public List<ChatHistoryResponse> getHistory(Long userId) {
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        return chatMessageRepository.findByUser_IdOrderByCreatedAtAsc(userId).stream()
                .map(m -> new ChatHistoryResponse(m.getSessionId(), m.getRole(), m.getMessage(), m.getCreatedAt()))
                .toList();
    }

    private Map<String, Object> turn(String role, String text) {
        String geminiRole = "MODEL".equalsIgnoreCase(role) ? "model" : "user";
        return Map.of("role", geminiRole, "parts", List.of(Map.of("text", text)));
    }

    private String buildSystemPrompt() {
        List<ProductEntity> products = productRepository.findAll().stream().limit(50).toList();
        StringBuilder sb = new StringBuilder();
        sb.append("당신은 쇼핑몰 '앱팡(AppPang)'의 친절한 상품 추천 도우미입니다.\n");
        sb.append("사용자의 질문에 맞는 상품을 아래 목록에서만 추천하세요. 목록에 없는 상품은 추천하지 마세요.\n");
        sb.append("추천 이유도 간단히 설명하고, 항상 한국어로 답변하세요.\n\n");
        sb.append("반드시 아래 JSON 형식으로만 응답하세요. 그 외 텍스트는 절대 포함하지 마세요.\n");
        sb.append("{\n");
        sb.append("  \"message\": \"사용자에게 보여줄 추천 설명 (한국어)\",\n");
        sb.append("  \"productIds\": [추천하는 상품의 id 숫자 배열]\n");
        sb.append("}\n");
        sb.append("- message에는 상품명을 나열하지 말고 추천 이유 위주로 자연스럽게 작성하세요 (상품 카드는 별도로 표시됩니다).\n");
        sb.append("- 추천할 상품이 없으면 productIds는 빈 배열([])로 두세요.\n");
        sb.append("- productIds에는 반드시 아래 목록에 있는 id만 넣으세요.\n\n");
        sb.append("[판매 중인 상품 목록]\n");
        for (ProductEntity p : products) {
            sb.append("- id:").append(p.getId())
                    .append(", 상품명:").append(p.getName())
                    .append(", 브랜드:").append(p.getBrand())
                    .append(", 가격:").append(p.getPrice()).append("원\n");
        }
        return sb.toString();
    }

    /**
     * Gemini가 반환한 JSON 문자열을 파싱한다.
     * AI 응답은 신뢰할 수 없는 입력이므로, 형식이 깨지면 원문을 message로 쓰고 추천은 비운다(폴백).
     */
    private GeminiReply parseGeminiReply(String rawReply) {
        try {
            JsonNode root = objectMapper.readTree(rawReply);
            String message = root.path("message").asText("");
            if (message.isBlank()) {
                // JSON은 맞지만 message가 비었으면 원문으로 폴백
                message = rawReply;
            }
            List<Long> productIds = new ArrayList<>();
            JsonNode idsNode = root.path("productIds");
            if (idsNode.isArray()) {
                for (JsonNode idNode : idsNode) {
                    if (idNode.canConvertToLong()) {
                        productIds.add(idNode.asLong());
                    }
                }
            }
            return new GeminiReply(message, productIds);
        } catch (Exception e) {
            log.warn("Gemini 응답 JSON 파싱 실패. 원문을 그대로 사용합니다. raw={}", rawReply, e);
            return new GeminiReply(rawReply, List.of());
        }
    }

    /**
     * 추천 상품 id로 상품 정보 + 대표이미지를 조회해 카드로 만든다.
     * - 실제 DB에 존재하는 id만 남긴다 (AI가 없는 id를 줄 수 있음)
     * - Gemini가 준 추천 우선순위(순서)를 유지한다
     * - 상품/이미지는 각각 한 번의 쿼리로 배치 조회 (N+1 방지)
     */
    private List<RecommendedProduct> buildRecommendedProducts(List<Long> productIds) {
        if (productIds.isEmpty()) {
            return List.of();
        }

        // 상품 배치 조회 → id로 빠르게 찾을 수 있게 맵 구성
        Map<Long, ProductEntity> productMap = new LinkedHashMap<>();
        for (ProductEntity p : productRepository.findAllById(productIds)) {
            productMap.put(p.getId(), p);
        }

        // 대표이미지(is_main=true) 배치 조회 → productId → imageUrl 맵
        Map<Long, String> mainImageMap = new LinkedHashMap<>();
        for (ProductImageEntity img : productImageRepository.findByProductIdInAndIsMainTrue(productIds)) {
            mainImageMap.putIfAbsent(img.getProduct().getId(), img.getImageUrl());
        }

        // Gemini가 준 순서대로, 실존 상품만 카드 생성
        List<RecommendedProduct> cards = new ArrayList<>();
        for (Long id : productIds) {
            ProductEntity p = productMap.get(id);
            if (p == null) {
                continue;
            }
            cards.add(new RecommendedProduct(
                    p.getId(),
                    p.getName(),
                    p.getBrand(),
                    p.getPrice(),
                    mainImageMap.get(id)
            ));
        }
        return cards;
    }

    /** Gemini JSON 파싱 결과 (내부용) */
    private record GeminiReply(String message, List<Long> productIds) {}
}