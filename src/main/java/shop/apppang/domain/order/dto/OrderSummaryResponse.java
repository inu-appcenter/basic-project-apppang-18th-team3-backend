package shop.apppang.domain.order.dto;

import shop.apppang.domain.order.entity.OrderEntity;
import shop.apppang.domain.order.entity.OrderItemEntity;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record OrderSummaryResponse(
        Long orderId,
        LocalDateTime orderDate,
        String status,
        Long totalPrice,
        List<OrderItemInfo> items
) {
    public static OrderSummaryResponse from(OrderEntity o, List<OrderItemEntity> items, Map<Long, String> mainImageMap) {
        return new OrderSummaryResponse(
                o.getId(), o.getCreatedAt(), o.getStatus(), o.getTotalPrice(),
                items.stream().map(oi -> OrderItemInfo.from(oi, mainImageMap)).toList()
        );
    }
}