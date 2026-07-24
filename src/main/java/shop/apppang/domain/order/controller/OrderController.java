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
            @ApiResponse(responseCode = "201", description = "주문 생성 성공",
                    content = @Content(schema = @Schema(implementation = OrderCreateResponse.class),
                            examples = @ExampleObject(value = """
                                    { "orderId": 100, "totalPrice": 42000, "status": "주문접수", "remainingMoney": 8000 }
                                    """))),
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
    @ApiResponse(responseCode = "200", description = "주문 목록 조회 성공 (없으면 빈 배열)",
            content = @Content(schema = @Schema(implementation = OrderSummaryResponse.class),
                    examples = @ExampleObject(value = """
                            [
                              {
                                "orderId": 100,
                                "orderDate": "2026-06-15T14:00:00",
                                "status": "진행중",
                                "totalPrice": 42000,
                                "items": [
                                  { "orderItemId": 1, "productId": 5, "productName": "여름 티셔츠", "quantity": 2, "price": 15000, "status": "배송중" }
                                ]
                              }
                            ]
                            """)))
    @GetMapping
    public ResponseEntity<List<OrderSummaryResponse>> getOrders(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(orderService.getOrders(userId));
    }

    @Operation(summary = "주문 상세 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "주문 상세 조회 성공",
                    content = @Content(schema = @Schema(implementation = OrderDetailResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "orderId": 100,
                                      "orderDate": "2026-06-15T14:00:00",
                                      "status": "진행중",
                                      "productAmount": 50000,
                                      "discountAmount": 8000,
                                      "shippingFee": 0,
                                      "paymentMethod": "app_money",
                                      "totalPrice": 42000,
                                      "recipientName": "고명재",
                                      "phone": "01012345678",
                                      "zipcode": "06241",
                                      "address": "서울시 강남구 ...",
                                      "detailAddress": "101동 202호",
                                      "deliveryRequest": "문 앞에 놓아주세요",
                                      "items": [
                                        { "orderItemId": 1, "productId": 5, "productName": "여름 티셔츠", "quantity": 2, "price": 15000, "status": "배송중" },
                                        { "orderItemId": 2, "productId": 8, "productName": "운동화", "quantity": 1, "price": 30000, "status": "배송완료" }
                                      ]
                                    }
                                    """))),
            @ApiResponse(responseCode = "404", description = "주문 정보를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"error\": \"주문 정보를 불러올 수 없습니다. 다시 시도해주세요\"}")))
    })
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDetailResponse> getOrderDetail(
            @AuthenticationPrincipal Long userId,
            @Parameter(description = "주문 ID", required = true) @PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderDetail(userId, orderId));
    }

    @Operation(summary = "결제 예상 금액 조회 (서버 계산)")
    @ApiResponse(responseCode = "200", description = "예상 금액 계산 성공",
            content = @Content(schema = @Schema(implementation = EstimateResponse.class),
                    examples = @ExampleObject(value = """
                            { "productAmount": 50000, "discountAmount": 8000, "shippingFee": 0, "totalPrice": 42000 }
                            """)))
    @PostMapping("/estimate")
    public ResponseEntity<EstimateResponse> estimate(@RequestBody EstimateRequest request) {
        return ResponseEntity.ok(orderService.estimate(request));
    }

    @Operation(summary = "주문 취소")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "주문 취소 성공 (모든 order_item이 '주문접수' 상태일 때만 가능)",
                    content = @Content(schema = @Schema(implementation = OrderCancelResponse.class),
                            examples = @ExampleObject(value = """
                                    { "orderId": 100, "status": "취소됨", "refundMoney": 42000, "remainingMoney": 92000 }
                                    """))),
            @ApiResponse(responseCode = "409", description = "이미 배송이 시작되어 취소 불가",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"error\": \"이미 배송이 시작되어 취소할 수 없습니다\"}")))
    })
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<OrderCancelResponse> cancelOrder(
            @AuthenticationPrincipal Long userId,
            @Parameter(description = "주문 ID", required = true) @PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.cancelOrder(userId, orderId));
    }
}