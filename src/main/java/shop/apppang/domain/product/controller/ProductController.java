package shop.apppang.domain.product.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.apppang.domain.product.dto.ProductDetailResponse;
import shop.apppang.domain.product.dto.ProductListResponse;
import shop.apppang.domain.product.service.ProductService;
import shop.apppang.global.exception.ErrorResponse;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // 상품 목록 조회
    @Operation(
            summary = "상품 목록 조회 (카테고리·검색, 필터, 정렬·페이지 공용)",
            description = """
                카테고리 ID 목록<br>
                1 : 식품<br>
                2 : 생활용품<br>
                3 : 뷰티<br>
                4 : 의류 및 잡화<br>
                5 : 가전 및 디지털<br>
                6 : 홈인테리어<br>
                7 : 출산 및 유아<br>
                8 : 반려동물<br>
                9 : 스포츠 및 레저<br>
                10 : 자동차용품
                """
    )
    @ApiResponse(responseCode = "200", description = "상품 목록 조회 성공 (결과 없으면 items: [], total: 0)",
            content = @Content(schema = @Schema(implementation = ProductListResponse.class),
                    examples = @ExampleObject(value = """
                            {
                              "categoryId": 2,
                              "categoryName": "신발",
                              "keyword": null,
                              "page": 1,
                              "size": 20,
                              "total": 134,
                              "hasNext": true,
                              "items": [
                                {
                                  "productId": 5,
                                  "name": "여름 티셔츠",
                                  "imageUrl": "https://.../5.jpg",
                                  "optionInfo": "100g, 1개",
                                  "price": 12000,
                                  "originalPrice": 15000,
                                  "discountRate": 20,
                                  "unitPrice": "100g당 12,000원",
                                  "averageRating": 4.3,
                                  "reviewCount": 28,
                                  "shippingInfo": "무료배송 / 반품 가능",
                                  "rocketDelivery": true
                                }
                              ]
                            }
                            """)))
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
    // 상품 상세 조회
    @Operation(
            summary = "상품 상세 조회",
            description = """
                카테고리 ID 목록<br>
                1 : 식품<br>
                2 : 생활용품<br>
                3 : 뷰티<br>
                4 : 의류 및 잡화<br>
                5 : 가전 및 디지털<br>
                6 : 홈인테리어<br>
                7 : 출산 및 유아<br>
                8 : 반려동물<br>
                9 : 스포츠 및 레저<br>
                10 : 자동차용품
                """
    ) // description은 markdown 형식을 지원하지만 가끔 렌더링 오류가 있다고 해서 HTML 태그 사용
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "상품 상세 조회 성공",
                    content = @Content(schema = @Schema(implementation = ProductDetailResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "productId": 5,
                                      "brand": "나이키",
                                      "name": "여름 티셔츠",
                                      "images": ["https://.../5_1.jpg", "https://.../5_2.jpg"],
                                      "optionInfo": "100g, 1개",
                                      "price": 12000,
                                      "originalPrice": 15000,
                                      "discountRate": 20,
                                      "unitPrice": "100g당 12,000원",
                                      "shippingInfo": "무료배송 / 반품 가능",
                                      "rocketDelivery": true,
                                      "stock": 32,
                                      "description": "시원한 여름용 티셔츠",
                                      "detailImages": ["https://.../detail1.jpg"],
                                      "categoryId": 2,
                                      "isWished": false,
                                      "canWriteReview": false,
                                      "reviewSummary": { "averageRating": 4.3, "reviewCount": 28 }
                                    }
                                    """))),
            @ApiResponse(responseCode = "404", description = "상품 정보를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"error\": \"상품 정보를 불러올 수 없습니다\"}")))
    })
    @GetMapping("/{product_id}")
    public ResponseEntity<ProductDetailResponse> getProduct(
            @RequestAttribute(name = "userId", required = false) Long userId,
            @PathVariable("product_id") Long productId
    ) {
        return ResponseEntity.ok(productService.getProduct(userId, productId));
    }
}