package shop.apppang.domain.product.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import shop.apppang.domain.product.dto.ProductDetailResponse;
import shop.apppang.domain.product.dto.ProductListResponse;
import shop.apppang.domain.product.service.ProductService;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // 상품 목록 조회
    @Operation(summary = "상품 목록 조회 (카테고리·검색, 필터, 정렬·페이지 공용)")
    @GetMapping
    public ProductListResponse getProducts() {
        return productService.getProducts();
    }

    // 상품 상세 조회
    @Operation(summary = "상품 상세 조회")
    @GetMapping("/{productId}")
    public ProductDetailResponse getProduct(
            @Parameter(description = "상품 ID", required = true) @PathVariable Long productId,
            Authentication authentication
    ) {

        Long userId = (Long) authentication.getPrincipal();

        return productService.getProduct(userId, productId);
    }

}