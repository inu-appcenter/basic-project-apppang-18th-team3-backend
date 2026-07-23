package shop.apppang.domain.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record OrderItemRequest(
        @Schema(description = "상품 ID", example = "5") Long productId,
        @Schema(description = "수량", example = "2") Integer quantity
) {}