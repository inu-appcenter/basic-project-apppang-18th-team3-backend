package shop.apppang.domain.order.dto;

import shop.apppang.domain.order.entity.OrderEntity;
import shop.apppang.domain.order.entity.OrderItemEntity;
import java.time.LocalDateTime;
import java.util.List;

public record OrderSummaryResponse(
        Long orderId,
        LocalDateTime orderDate,
        String status,
        Long totalPrice,
        List<OrderItemInfo> items
) {
    public static OrderSummaryResponse from(OrderEntity o, List<OrderItemEntity> items) {
        return new OrderSummaryResponse(
                o.getId(), o.getCreatedAt(), o.getStatus(), o.getTotalPrice(),
                items.stream().map(OrderItemInfo::from).toList()
        );
    }
}