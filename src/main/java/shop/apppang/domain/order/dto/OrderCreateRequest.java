package shop.apppang.domain.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record OrderCreateRequest(
        @Schema(description = "배송지 ID", example = "3") Long addressId,
        @Schema(description = "주문할 상품 목록") List<OrderItemRequest> items,
        @Schema(description = "결제 수단", example = "app_money") String paymentMethod,
        @Schema(description = "배송 요청사항", example = "문 앞에 놓아주세요") String deliveryRequest
) {}