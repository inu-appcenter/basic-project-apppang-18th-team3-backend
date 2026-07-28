package shop.apppang.domain.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import shop.apppang.domain.chat.client.GeminiClient;
import shop.apppang.domain.chat.dto.ChatHistoryPageResponse;
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
    public ChatHistoryPageResponse getHistory(Long userId, Long cursor, int size) {
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }

        // DB에서는 최신순(id DESC)으로 size개 조회 (첫 로드 / 커서 이후)
        PageRequest pageable = PageRequest.of(0, size);
        Slice<ChatMessageEntity> slice = (cursor == null)
                ? chatMessageRepository.findByUser_IdOrderByIdDesc(userId, pageable)
                : chatMessageRepository.findByUser_IdAndIdLessThanOrderByIdDesc(userId, cursor, pageable);

        // 화면 표시용으로 시간순(ASC)으로 뒤집어서 응답
        List<ChatMessageEntity> desc = slice.getContent();
        List<ChatHistoryResponse> messages = new ArrayList<>();
        for (int i = desc.size() - 1; i >= 0; i--) {
            ChatMessageEntity m = desc.get(i);
            messages.add(new ChatHistoryResponse(m.getSessionId(), m.getRole(), m.getMessage(), m.getCreatedAt()));
        }

        // nextCursor = 이번 배치에서 가장 오래된 id (DESC 마지막). 더 없으면 null
        Long nextCursor = (slice.hasNext() && !desc.isEmpty())
                ? desc.get(desc.size() - 1).getId()
                : null;

        return new ChatHistoryPageResponse(messages, nextCursor, slice.hasNext());
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