package shop.apppang.domain.chat.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import shop.apppang.domain.chat.dto.ChatHistoryResponse;
import shop.apppang.domain.chat.dto.ChatRequest;
import shop.apppang.domain.chat.dto.ChatResponse;
import shop.apppang.domain.chat.service.ChatService;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    // 채팅 — 비로그인도 가능, 로그인 시 기록 저장
    @Operation(summary = "챗봇 메시지 전송 (자연어 → 상품 추천)")
    @PostMapping
    public ResponseEntity<ChatResponse> chat(
            @AuthenticationPrincipal Long userId,
            @RequestBody ChatRequest request) {
        return ResponseEntity.ok(chatService.chat(userId, request));
    }

    // 대화 기록 조회 — 로그인 필수
    @Operation(summary = "이전 대화 목록 조회")
    @GetMapping("/history")
    public ResponseEntity<List<ChatHistoryResponse>> history(
            @AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(chatService.getHistory(userId));
    }
}