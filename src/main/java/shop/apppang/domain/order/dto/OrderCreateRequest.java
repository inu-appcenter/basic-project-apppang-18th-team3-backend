package shop.apppang.domain.order.dto;

import java.util.List;

public record OrderCreateRequest(
        Long addressId,
        List<OrderItemRequest> items,
        String paymentMethod,
        String deliveryRequest
) {}