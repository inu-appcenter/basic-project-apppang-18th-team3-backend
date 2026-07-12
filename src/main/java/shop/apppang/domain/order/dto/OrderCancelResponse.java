package shop.apppang.domain.order.dto;

public record OrderCancelResponse(
        Long orderId,
        String status,
        Long refundMoney,
        Long remainingMoney
) {}