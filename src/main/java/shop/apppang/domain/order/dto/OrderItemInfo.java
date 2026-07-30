package shop.apppang.domain.order.dto;

import shop.apppang.domain.order.entity.OrderItemEntity;
import java.util.Map;

public record OrderItemInfo(
        Long orderItemId,
        Long productId,
        String productName,
        String imageUrl,
        Integer quantity,
        Long price,
        String status
) {
    public static OrderItemInfo from(OrderItemEntity oi, Map<Long, String> mainImageMap) {
        Long productId = oi.getProduct().getId();
        return new OrderItemInfo(
                oi.getId(), productId, oi.getProduct().getName(), mainImageMap.get(productId),
                oi.getQuantity(), oi.getPrice(), oi.getStatus()
        );
    }
}