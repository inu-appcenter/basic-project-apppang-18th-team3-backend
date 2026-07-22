package shop.apppang.domain.cart.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record CartUpdateRequest(
        @Schema(description = "변경할 수량 (1 이상)", example = "3") Integer quantity
) {}