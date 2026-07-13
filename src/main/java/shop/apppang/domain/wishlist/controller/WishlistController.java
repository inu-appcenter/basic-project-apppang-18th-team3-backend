package shop.apppang.domain.wishlist.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import shop.apppang.domain.wishlist.dto.WishlistRequest;
import shop.apppang.domain.wishlist.dto.WishlistResponse;
import shop.apppang.domain.wishlist.service.WishlistService;
import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @GetMapping
    public ResponseEntity<List<WishlistResponse>> getWishlist(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(wishlistService.getWishlist(userId));
    }

    @PostMapping
    public ResponseEntity<WishlistResponse> addWishlist(
            @AuthenticationPrincipal Long userId,
            @RequestBody WishlistRequest request) {
        return ResponseEntity.status(201).body(wishlistService.addWishlist(userId, request.productId()));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> removeWishlist(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long productId) {
        wishlistService.removeWishlist(userId, productId);
        return ResponseEntity.noContent().build();
    }
}