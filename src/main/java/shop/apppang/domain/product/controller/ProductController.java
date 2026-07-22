package shop.apppang.domain.product.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
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

    // 상품 목록 조회
    @Operation(summary = "상품 목록 조회 (카테고리·검색, 필터, 정렬·페이지 공용)")
    @GetMapping
    public ProductListResponse getProducts() {
        return productService.getProducts();
    }

    // 상품 상세 조회
    @Operation(summary = "상품 상세 조회")
    @ApiResponse(responseCode = "404", description = "상품 정보를 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"error\": \"상품 정보를 불러올 수 없습니다\"}")))
    @GetMapping("/{productId}")
    public ProductDetailResponse getProduct(
            @Parameter(description = "상품 ID", required = true) @PathVariable Long productId,
            @AuthenticationPrincipal Long userId
    ) {

        return productService.getProduct(userId, productId);
    }

}