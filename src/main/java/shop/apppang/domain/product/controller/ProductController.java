package shop.apppang.domain.product.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import shop.apppang.domain.product.dto.ProductDetailResponse;
import shop.apppang.domain.product.dto.ProductListResponse;
import shop.apppang.domain.product.service.ProductService;
import org.springframework.data.domain.Pageable;


@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // 1. 상품 목록 조회 (필터링 및 페이징 파라미터 수용)
    @GetMapping
    public ProductListResponse getProducts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword,
            Pageable pageable
    ) {
        return productService.getProducts(categoryId, keyword, pageable);
    }

    // 2. 추천 상품 목록 조회 (선택적 인증)
    @GetMapping("/recommended")
    public Object getRecommendedProducts(
            Authentication authentication,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        Long userId = extractUserId(authentication);
        return productService.getRecommendedProducts(userId, size);
    }

    // 3. 인기 상품 목록 조회 (인증 불필요)
    @GetMapping("/popular")
    public Object getPopularProducts(
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        return productService.getPopularProducts(size);
    }

    // 4. 상품 상세 조회 (선택적/필수 인증 - 선택적 처리 가능)
    @GetMapping("/{productId}")
    public ProductDetailResponse getProduct(
            @PathVariable Long productId,
            Authentication authentication
    ) {
        Long userId = extractUserId(authentication);
        return productService.getProduct(userId, productId);
    }

    private Long extractUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof Long) {
            return (Long) principal;
        }

        return null;
    }
}