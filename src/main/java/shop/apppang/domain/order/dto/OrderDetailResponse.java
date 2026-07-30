package shop.apppang.domain.order.dto;

import shop.apppang.domain.order.entity.OrderEntity;
import shop.apppang.domain.order.entity.OrderItemEntity;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record OrderDetailResponse(
        Long orderId,
        LocalDateTime orderDate,
        String status,
        Long productAmount,
        Long discountAmount,
        Long shippingFee,
        String paymentMethod,
        Long totalPrice,
        String recipientName,
        String phone,
        String zipcode,
        String address,
        String detailAddress,
        String deliveryRequest,
        List<OrderItemInfo> items
) {
    public static OrderDetailResponse from(OrderEntity o, List<OrderItemEntity> items, Map<Long, String> mainImageMap) {
        return new OrderDetailResponse(
                o.getId(), o.getCreatedAt(), o.getStatus(),
                o.getProductAmount(), o.getDiscountAmount(), o.getShippingFee(),
                o.getPaymentMethod(), o.getTotalPrice(),
                o.getShippingRecipientName(), o.getShippingPhone(), o.getShippingZipcode(),
                o.getShippingAddress(), o.getShippingDetailAddress(), o.getDeliveryRequest(),
                items.stream().map(oi -> OrderItemInfo.from(oi, mainImageMap)).toList()
        );
    }
}