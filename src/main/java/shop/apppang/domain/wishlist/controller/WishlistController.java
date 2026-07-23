package shop.apppang.domain.wishlist.controller;

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
import shop.apppang.domain.wishlist.dto.WishlistRequest;
import shop.apppang.domain.wishlist.dto.WishlistResponse;
import shop.apppang.domain.wishlist.service.WishlistService;
import shop.apppang.global.exception.ErrorResponse;
import java.util.List;

@Tag(name = "찜")
@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @Operation(summary = "내 찜 목록")
    @ApiResponse(responseCode = "200", description = "찜 목록 조회 성공 (없으면 빈 배열)",
            content = @Content(schema = @Schema(implementation = WishlistResponse.class),
                    examples = @ExampleObject(value = """
                            [
                              { "wishlistId": 3, "productId": 5, "productName": "여름 티셔츠", "brand": "나이키", "price": 15000, "rocketDelivery": true }
                            ]
                            """)))
    @GetMapping
    public ResponseEntity<List<WishlistResponse>> getWishlist(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(wishlistService.getWishlist(userId));
    }

    @Operation(summary = "찜하기")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "찜하기 성공",
                    content = @Content(schema = @Schema(implementation = WishlistResponse.class),
                            examples = @ExampleObject(value = """
                                    { "wishlistId": 3, "productId": 5, "productName": "여름 티셔츠", "brand": "나이키", "price": 15000, "rocketDelivery": true }
                                    """))),
            @ApiResponse(responseCode = "409", description = "이미 찜한 상품",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"error\": \"이미 찜한 상품입니다\"}")))
    })
    @PostMapping
    public ResponseEntity<WishlistResponse> addWishlist(
            @AuthenticationPrincipal Long userId,
            @RequestBody WishlistRequest request) {
        return ResponseEntity.status(201).body(wishlistService.addWishlist(userId, request.productId()));
    }

    @Operation(summary = "찜 해제")
    @ApiResponse(responseCode = "204", description = "찜 해제 성공 (본문 없음)")
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> removeWishlist(
            @AuthenticationPrincipal Long userId,
            @Parameter(description = "상품 ID", required = true) @PathVariable Long productId) {
        wishlistService.removeWishlist(userId, productId);
        return ResponseEntity.noContent().build();
    }
}