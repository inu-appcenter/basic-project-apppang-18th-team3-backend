package shop.apppang.domain.cart.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record CartAddRequest(
        @Schema(description = "상품 ID", example = "5") Long productId,
        @Schema(description = "담을 수량", example = "2") Integer quantity
) {}