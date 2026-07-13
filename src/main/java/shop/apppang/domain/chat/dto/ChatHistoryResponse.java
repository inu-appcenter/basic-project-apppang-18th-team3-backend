package shop.apppang.domain.chat.dto;

import java.time.LocalDateTime;

public record ChatHistoryResponse(String sessionId, String role, String message, LocalDateTime createdAt) {}