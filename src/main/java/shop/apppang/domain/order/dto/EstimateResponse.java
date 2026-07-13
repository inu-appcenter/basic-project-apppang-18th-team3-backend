package shop.apppang.domain.order.dto;

public record EstimateResponse(
        Long productAmount,
        Long discountAmount,
        Long shippingFee,
        Long totalPrice
) {}