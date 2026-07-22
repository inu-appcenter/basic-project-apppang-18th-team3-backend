package shop.apppang.domain.wishlist.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
    @GetMapping
    public ResponseEntity<List<WishlistResponse>> getWishlist(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(wishlistService.getWishlist(userId));
    }

    @Operation(summary = "찜하기")
    @ApiResponse(responseCode = "409", description = "이미 찜한 상품",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"error\": \"이미 찜한 상품입니다\"}")))
    @PostMapping
    public ResponseEntity<WishlistResponse> addWishlist(
            @AuthenticationPrincipal Long userId,
            @RequestBody WishlistRequest request) {
        return ResponseEntity.status(201).body(wishlistService.addWishlist(userId, request.productId()));
    }

    @Operation(summary = "찜 해제")
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> removeWishlist(
            @AuthenticationPrincipal Long userId,
            @Parameter(description = "상품 ID", required = true) @PathVariable Long productId) {
        wishlistService.removeWishlist(userId, productId);
        return ResponseEntity.noContent().build();
    }
}