package shop.apppang.domain.order.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

    @Operation(summary = "주문 생성")
    @PostMapping
    public ResponseEntity<OrderCreateResponse> createOrder(
            @AuthenticationPrincipal Long userId,
            @RequestBody OrderCreateRequest request) {
        return ResponseEntity.status(201).body(orderService.createOrder(userId, request));
    }

    @Operation(summary = "내 주문 목록")
    @GetMapping
    public ResponseEntity<List<OrderSummaryResponse>> getOrders(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(orderService.getOrders(userId));
    }

    @Operation(summary = "주문 상세 조회")
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDetailResponse> getOrderDetail(
            @AuthenticationPrincipal Long userId,
            @Parameter(description = "주문 ID", required = true) @PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderDetail(userId, orderId));
    }

    @Operation(summary = "결제 예상 금액 조회 (서버 계산)")
    @PostMapping("/estimate")
    public ResponseEntity<EstimateResponse> estimate(@RequestBody EstimateRequest request) {
        return ResponseEntity.ok(orderService.estimate(request));
    }

    @Operation(summary = "주문 취소")
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<OrderCancelResponse> cancelOrder(
            @AuthenticationPrincipal Long userId,
            @Parameter(description = "주문 ID", required = true) @PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.cancelOrder(userId, orderId));
    }
}