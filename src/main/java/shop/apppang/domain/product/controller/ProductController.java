package shop.apppang.domain.product.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import shop.apppang.domain.product.dto.ProductDetailResponse;
import shop.apppang.domain.product.dto.ProductListResponse;
import shop.apppang.domain.product.service.ProductService;
import shop.apppang.global.exception.ErrorResponse;

@Tag(name = "상품")
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // 1. 상품 목록 조회
    @Operation(summary = "상품 목록 조회 (카테고리·검색, 필터, 정렬·페이지 공용)")
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

    // 2. 상품 상세 조회
    @Operation(summary = "상품 상세 조회")
    @ApiResponse(
            responseCode = "404", 
            description = "상품 정보를 찾을 수 없음",
            content = @Content(
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"error\": \"상품 정보를 불러올 수 없습니다\"}")
            )
    )
    @GetMapping("/{product_id}")
    public ResponseEntity<ProductDetailResponse> getProduct(
            @Parameter(description = "상품 ID", required = true) @PathVariable("product_id") Long productId,
            @AuthenticationPrincipal Long userId
    ) {
        return ResponseEntity.ok(productService.getProduct(userId, productId));
    }
}