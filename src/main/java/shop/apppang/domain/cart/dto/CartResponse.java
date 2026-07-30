package shop.apppang.domain.cart.dto;

import shop.apppang.domain.cart.entity.CartItemEntity;
import java.util.List;
import java.util.Map;

public record CartResponse(
        int itemCount,        // 담긴 상품 종류 수
        long totalPrice,      // 전체 합계
        List<CartItemResponse> items
) {
    public static CartResponse from(List<CartItemEntity> cartItems, Map<Long, String> mainImageMap) {
        List<CartItemResponse> items = cartItems.stream()
                .map(c -> CartItemResponse.from(c, mainImageMap.get(c.getProduct().getId())))
                .toList();
        long total = items.stream().mapToLong(CartItemResponse::subtotal).sum();
        return new CartResponse(items.size(), total, items);
    }
}