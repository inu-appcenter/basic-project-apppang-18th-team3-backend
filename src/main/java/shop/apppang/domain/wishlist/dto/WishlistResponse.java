package shop.apppang.domain.wishlist.dto;

import shop.apppang.domain.product.entity.ProductEntity;
import shop.apppang.domain.wishlist.entity.WishlistEntity;

public record WishlistResponse(
        Long wishlistId,
        Long productId,
        String productName,
        String brand,
        Long price,
        Boolean rocketDelivery
) {
    public static WishlistResponse from(WishlistEntity w) {
        ProductEntity p = w.getProduct();
        return new WishlistResponse(
                w.getId(), p.getId(), p.getName(), p.getBrand(), p.getPrice(), p.getRocketDelivery()
        );
    }
}