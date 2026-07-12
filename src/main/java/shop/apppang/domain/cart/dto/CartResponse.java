package shop.apppang.domain.cart.dto;

import shop.apppang.domain.cart.entity.CartItemEntity;
import java.util.List;

public record CartResponse(
        int itemCount,        // 담긴 상품 종류 수
        long totalPrice,      // 전체 합계
        List<CartItemResponse> items
) {
    public static CartResponse from(List<CartItemEntity> cartItems) {
        List<CartItemResponse> items = cartItems.stream().map(CartItemResponse::from).toList();
        long total = items.stream().mapToLong(CartItemResponse::subtotal).sum();
        return new CartResponse(items.size(), total, items);
    }
}