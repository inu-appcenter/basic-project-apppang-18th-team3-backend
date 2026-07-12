package shop.apppang.domain.product.controller;

import lombok.RequiredArgsConstructor;
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
    @GetMapping
    public ProductListResponse getProducts() {

        return productService.getProducts();

    }

    // 상품 상세 조회
    @GetMapping("/{productId}")
    public ProductDetailResponse getProduct(
            @PathVariable Long productId
    ) {

        return productService.getProduct(productId);

    }

}