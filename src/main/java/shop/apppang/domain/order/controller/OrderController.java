package shop.apppang.domain.order.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import shop.apppang.domain.order.dto.*;
import shop.apppang.domain.order.service.OrderService;
import shop.apppang.global.exception.ErrorResponse;
import java.util.List;

@Tag(name = "주문")
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "주문 생성")
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "배송지 미선택 / 결제 수단 미선택 중 하나",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"error\": \"배송지를 선택해주세요\"}"))),
            @ApiResponse(responseCode = "402", description = "앱팡 머니 잔액 부족",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"error\": \"앱팡 머니 잔액이 부족합니다\"}"))),
            @ApiResponse(responseCode = "409", description = "재고 부족",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"error\": \"재고가 부족합니다\"}")))
    })
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
    @ApiResponse(responseCode = "404", description = "주문 정보를 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"error\": \"주문 정보를 불러올 수 없습니다. 다시 시도해주세요\"}")))
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
    @ApiResponse(responseCode = "409", description = "이미 배송이 시작되어 취소 불가",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"error\": \"이미 배송이 시작되어 취소할 수 없습니다\"}")))
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<OrderCancelResponse> cancelOrder(
            @AuthenticationPrincipal Long userId,
            @Parameter(description = "주문 ID", required = true) @PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.cancelOrder(userId, orderId));
    }
}