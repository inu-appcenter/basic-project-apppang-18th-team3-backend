package shop.apppang.domain.cart.dto;

import shop.apppang.domain.cart.entity.CartItemEntity;
import shop.apppang.domain.product.entity.ProductEntity;

public record CartItemResponse(
        Long cartItemId,
        Long productId,
        String productName,
        Long price,
        Integer quantity,
        Long subtotal      // 상품별 합계 = 가격 × 수량 (계산값)
) {
    public static CartItemResponse from(CartItemEntity c) {
        ProductEntity p = c.getProduct();
        long subtotal = p.getPrice() * c.getQuantity();
        return new CartItemResponse(
                c.getId(), p.getId(), p.getName(), p.getPrice(), c.getQuantity(), subtotal
        );
    }
}