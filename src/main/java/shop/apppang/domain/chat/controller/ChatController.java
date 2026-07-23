package shop.apppang.domain.chat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import shop.apppang.domain.chat.dto.ChatHistoryResponse;
import shop.apppang.domain.chat.dto.ChatRequest;
import shop.apppang.domain.chat.dto.ChatResponse;
import shop.apppang.domain.chat.service.ChatService;
import shop.apppang.global.exception.ErrorResponse;

import java.util.List;

@Tag(name = "챗봇")
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    // 채팅 — 비로그인도 가능, 로그인 시 기록 저장
    @Operation(summary = "챗봇 메시지 전송 (자연어 → 상품 추천)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "응답 성공 (조건에 맞는 상품이 없으면 products: [])",
                    content = @Content(schema = @Schema(implementation = ChatResponse.class),
                            examples = @ExampleObject(value = """
                                    { "sessionId": "abc123", "reply": "2만원 이하 로켓배송 커피를 찾아봤어요. 이런 상품은 어떠세요?" }
                                    """))),
            @ApiResponse(responseCode = "400", description = "빈 메시지",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"error\": \"메시지를 입력해주세요\"}"))),
            @ApiResponse(responseCode = "503", description = "챗봇/LLM 시스템 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"error\": \"일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요\"}")))
    })
    @PostMapping
    public ResponseEntity<ChatResponse> chat(
            @AuthenticationPrincipal Long userId,
            @RequestBody ChatRequest request) {
        return ResponseEntity.ok(chatService.chat(userId, request));
    }

    // 대화 기록 조회 — 로그인 필수
    @Operation(summary = "이전 대화 목록 조회")
    @ApiResponse(responseCode = "200", description = "대화 기록 조회 성공",
            content = @Content(schema = @Schema(implementation = ChatHistoryResponse.class),
                    examples = @ExampleObject(value = """
                            [
                              { "sessionId": "abc123", "role": "user", "message": "2만원 이하 커피 추천", "createdAt": "2026-06-23T10:00:00" },
                              { "sessionId": "abc123", "role": "bot", "message": "이런 상품은 어떠세요?", "createdAt": "2026-06-23T10:00:01" }
                            ]
                            """)))
    @GetMapping("/history")
    public ResponseEntity<List<ChatHistoryResponse>> history(
            @AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(chatService.getHistory(userId));
    }
}