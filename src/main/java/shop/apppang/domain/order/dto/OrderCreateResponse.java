package shop.apppang.domain.order.dto;

public record OrderCreateResponse(
        Long orderId,
        Long totalPrice,
        String status,
        Long remainingMoney
) {}