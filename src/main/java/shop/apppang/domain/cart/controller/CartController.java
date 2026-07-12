package shop.apppang.domain.cart.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.apppang.domain.cart.dto.CartAddRequest;
import shop.apppang.domain.cart.dto.CartItemResponse;
import shop.apppang.domain.cart.dto.CartResponse;
import shop.apppang.domain.cart.dto.CartUpdateRequest;
import shop.apppang.domain.cart.service.CartService;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartResponse> getCart(@RequestParam Long userId) {
        return ResponseEntity.ok(cartService.getCart(userId));
    }

    @PostMapping
    public ResponseEntity<CartItemResponse> addToCart(
            @RequestParam Long userId,
            @RequestBody CartAddRequest request) {
        return ResponseEntity.status(201)
                .body(cartService.addToCart(userId, request.productId(), request.quantity()));
    }

    @PatchMapping("/{cartItemId}")
    public ResponseEntity<CartItemResponse> updateQuantity(
            @RequestParam Long userId,
            @PathVariable Long cartItemId,
            @RequestBody CartUpdateRequest request) {
        return ResponseEntity.ok(cartService.updateQuantity(userId, cartItemId, request.quantity()));
    }

    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<Void> deleteCartItem(
            @RequestParam Long userId,
            @PathVariable Long cartItemId) {
        cartService.deleteCartItem(userId, cartItemId);
        return ResponseEntity.noContent().build();
    }
}