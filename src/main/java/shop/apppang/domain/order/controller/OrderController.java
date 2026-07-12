package shop.apppang.domain.order.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import shop.apppang.domain.order.dto.*;
import shop.apppang.domain.order.service.OrderService;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderCreateResponse> createOrder(
            @AuthenticationPrincipal Long userId,
            @RequestBody OrderCreateRequest request) {
        return ResponseEntity.status(201).body(orderService.createOrder(userId, request));
    }

    @GetMapping
    public ResponseEntity<List<OrderSummaryResponse>> getOrders(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(orderService.getOrders(userId));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDetailResponse> getOrderDetail(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderDetail(userId, orderId));
    }

    @PostMapping("/estimate")
    public ResponseEntity<EstimateResponse> estimate(@RequestBody EstimateRequest request) {
        return ResponseEntity.ok(orderService.estimate(request));
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<OrderCancelResponse> cancelOrder(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.cancelOrder(userId, orderId));
    }
}