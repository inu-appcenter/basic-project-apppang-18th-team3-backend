package shop.apppang.domain.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record ChatRequest(
        @Schema(description = "사용자 메시지", example = "2만원 이하 로켓배송 커피 추천해줘") String message,
        @Schema(description = "대화 세션 ID", example = "abc123") String sessionId
) {}