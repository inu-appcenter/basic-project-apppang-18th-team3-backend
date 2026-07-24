package shop.apppang.domain.product.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    public ProductListResponse getProducts() {
        return productService.getProducts();
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
    @GetMapping("/{productId}")
    public ProductDetailResponse getProduct(
            @Parameter(description = "상품 ID", required = true) @PathVariable Long productId,
            @AuthenticationPrincipal Long userId
    ) {

        return productService.getProduct(userId, productId);
    }

}