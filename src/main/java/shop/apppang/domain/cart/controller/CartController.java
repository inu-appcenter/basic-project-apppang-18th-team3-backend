package shop.apppang.domain.cart.controller;

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
import shop.apppang.domain.cart.dto.CartAddRequest;
import shop.apppang.domain.cart.dto.CartItemResponse;
import shop.apppang.domain.cart.dto.CartResponse;
import shop.apppang.domain.cart.dto.CartUpdateRequest;
import shop.apppang.domain.cart.service.CartService;
import shop.apppang.global.exception.ErrorResponse;

@Tag(name = "장바구니")
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @Operation(summary = "내 장바구니 조회")
    @ApiResponse(responseCode = "200", description = "장바구니 조회 성공 (비어 있으면 items: [], itemCount: 0)",
            content = @Content(schema = @Schema(implementation = CartResponse.class),
                    examples = @ExampleObject(value = """
                            {
                              "itemCount": 1,
                              "totalPrice": 24000,
                              "items": [
                                { "cartItemId": 10, "productId": 5, "productName": "여름 티셔츠", "price": 12000, "quantity": 2, "subtotal": 24000 }
                              ]
                            }
                            """)))
    @GetMapping
    public ResponseEntity<CartResponse> getCart(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(cartService.getCart(userId));
    }

    @Operation(summary = "장바구니 담기")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "담기 성공 (이미 담긴 수량과 합산됨)",
                    content = @Content(schema = @Schema(implementation = CartItemResponse.class),
                            examples = @ExampleObject(value = """
                                    { "cartItemId": 10, "productId": 5, "productName": "여름 티셔츠", "price": 12000, "quantity": 5, "subtotal": 60000 }
                                    """))),
            @ApiResponse(responseCode = "409", description = "기존 담긴 수량과 합산한 결과가 재고를 초과",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"error\": \"재고가 부족합니다\"}")))
    })
    @PostMapping
    public ResponseEntity<CartItemResponse> addToCart(
            @AuthenticationPrincipal Long userId,
            @RequestBody CartAddRequest request) {
        return ResponseEntity.status(201)
                .body(cartService.addToCart(userId, request.productId(), request.quantity()));
    }

    @Operation(summary = "장바구니 수량 변경")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수량 변경 성공",
                    content = @Content(schema = @Schema(implementation = CartItemResponse.class),
                            examples = @ExampleObject(value = """
                                    { "cartItemId": 10, "productId": 5, "productName": "여름 티셔츠", "price": 12000, "quantity": 3, "subtotal": 36000 }
                                    """))),
            @ApiResponse(responseCode = "400", description = "수량 1개 미만",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"error\": \"수량은 1개 이상이어야 합니다\"}"))),
            @ApiResponse(responseCode = "409", description = "변경할 수량이 재고를 초과",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"error\": \"재고가 부족합니다\"}")))
    })
    @PatchMapping("/{cartItemId}")
    public ResponseEntity<CartItemResponse> updateQuantity(
            @AuthenticationPrincipal Long userId,
            @Parameter(description = "장바구니 항목 ID", required = true) @PathVariable Long cartItemId,
            @RequestBody CartUpdateRequest request) {
        return ResponseEntity.ok(cartService.updateQuantity(userId, cartItemId, request.quantity()));
    }

    @Operation(summary = "장바구니 항목 삭제")
    @ApiResponse(responseCode = "204", description = "삭제 성공 (본문 없음)")
    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<Void> deleteCartItem(
            @AuthenticationPrincipal Long userId,
            @Parameter(description = "장바구니 항목 ID", required = true) @PathVariable Long cartItemId) {
        cartService.deleteCartItem(userId, cartItemId);
        return ResponseEntity.noContent().build();
    }
}