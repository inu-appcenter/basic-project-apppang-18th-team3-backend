package shop.apppang.domain.product.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.apppang.domain.product.dto.ProductDetailResponse;
import shop.apppang.domain.product.dto.ProductListResponse;
import shop.apppang.domain.product.service.ProductService;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<ProductListResponse> getProducts(
            @RequestParam(name = "category_id", required = false) Long categoryId,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "min_price", required = false) Long minPrice,
            @RequestParam(name = "max_price", required = false) Long maxPrice,
            @RequestParam(name = "rocket_delivery", required = false) Boolean rocketDelivery,
            @RequestParam(name = "sort", defaultValue = "sales") String sort,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "20") int size
    ) {
        // 프론트엔드의 1-based page를 Spring Data JPA의 0-based page로 전환
        int pageNumber = Math.max(0, page - 1);

        // 명세서의 sort 조건(sales, reviews, rating, price_desc, price_asc) 정렬 매핑
        Sort sortOrder = switch (sort) {
            case "price_asc" -> Sort.by("price").ascending();
            case "price_desc" -> Sort.by("price").descending();
            case "sales" -> Sort.by("salesCount").descending();
            default -> Sort.by("id").descending();
        };

        Pageable pageable = PageRequest.of(pageNumber, size, sortOrder);

        ProductListResponse response = productService.getProducts(
                categoryId, keyword, minPrice, maxPrice, rocketDelivery, sort, page, size, pageable
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{product_id}")
    public ResponseEntity<ProductDetailResponse> getProduct(
            @RequestAttribute(name = "userId", required = false) Long userId,
            @PathVariable("product_id") Long productId
    ) {
        return ResponseEntity.ok(productService.getProduct(userId, productId));
    }
}