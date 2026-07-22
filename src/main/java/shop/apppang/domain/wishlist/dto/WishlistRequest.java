package shop.apppang.domain.wishlist.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record WishlistRequest(
        @Schema(description = "상품 ID", example = "5") Long productId
) {}