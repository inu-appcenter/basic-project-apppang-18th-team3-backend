package shop.apppang.domain.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import shop.apppang.domain.chat.client.GeminiClient;
import shop.apppang.domain.chat.dto.ChatHistoryResponse;
import shop.apppang.domain.chat.dto.ChatRequest;
import shop.apppang.domain.chat.dto.ChatResponse;
import shop.apppang.domain.chat.entity.ChatMessageEntity;
import shop.apppang.domain.chat.repository.ChatMessageRepository;
import shop.apppang.domain.product.entity.ProductEntity;
import shop.apppang.domain.product.repository.ProductRepository;
import shop.apppang.domain.user.entity.User;
import shop.apppang.domain.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final GeminiClient geminiClient;
    private final ChatMessageRepository chatMessageRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

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

        // 4. Gemini 호출
        String reply = geminiClient.generate(systemPrompt, contents);

        // 5. 로그인 사용자만 대화 저장
        if (userId != null) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "사용자를 찾을 수 없습니다."));
            chatMessageRepository.save(ChatMessageEntity.builder()
                    .user(user).sessionId(sessionId).role("USER").message(request.message()).build());
            chatMessageRepository.save(ChatMessageEntity.builder()
                    .user(user).sessionId(sessionId).role("MODEL").message(reply).build());
        }

        return new ChatResponse(reply, sessionId);
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
        sb.append("[판매 중인 상품 목록]\n");
        for (ProductEntity p : products) {
            sb.append("- id:").append(p.getId())
                    .append(", 상품명:").append(p.getName())
                    .append(", 브랜드:").append(p.getBrand())
                    .append(", 가격:").append(p.getPrice()).append("원\n");
        }
        return sb.toString();
    }
}