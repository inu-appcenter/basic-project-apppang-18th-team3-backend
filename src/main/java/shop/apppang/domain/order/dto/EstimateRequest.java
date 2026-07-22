package shop.apppang.domain.order.dto;

import java.util.List;

public record EstimateRequest(List<OrderItemRequest> items) {}