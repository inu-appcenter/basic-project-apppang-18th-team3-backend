package shop.apppang.domain.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record EstimateRequest(
        @Schema(description = "예상 금액을 계산할 상품 목록") List<OrderItemRequest> items
) {}