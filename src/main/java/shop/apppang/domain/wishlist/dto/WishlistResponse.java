package shop.apppang.domain.wishlist.dto;

import shop.apppang.domain.product.entity.ProductEntity;
import shop.apppang.domain.wishlist.entity.WishlistEntity;

public record WishlistResponse(
        Long wishlistId,
        Long productId,
        String productName,
        String imageUrl,
        String brand,
        Long price,
        Boolean rocketDelivery
) {
    public static WishlistResponse from(WishlistEntity w, String imageUrl) {
        ProductEntity p = w.getProduct();
        return new WishlistResponse(
                w.getId(), p.getId(), p.getName(), imageUrl, p.getBrand(), p.getPrice(), p.getRocketDelivery()
        );
    }
}