package shop.apppang.domain.order.dto;

import shop.apppang.domain.order.entity.OrderItemEntity;

public record OrderItemInfo(
        Long orderItemId,
        Long productId,
        String productName,
        Integer quantity,
        Long price,
        String status
) {
    public static OrderItemInfo from(OrderItemEntity oi) {
        return new OrderItemInfo(
                oi.getId(), oi.getProduct().getId(), oi.getProduct().getName(),
                oi.getQuantity(), oi.getPrice(), oi.getStatus()
        );
    }
}