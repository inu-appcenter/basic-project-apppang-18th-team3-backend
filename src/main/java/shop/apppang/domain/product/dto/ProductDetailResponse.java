package shop.apppang.domain.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import shop.apppang.domain.review.dto.ReviewSummaryResponse;

import java.util.List;

@Getter
@AllArgsConstructor
public class ProductDetailResponse {

    private Long productId;                // 상품 ID

    private String brand;                  // 브랜드

    private String name;                   // 상품명

    private List<String> images;           // 상품 이미지

    private String optionInfo;             // 상품 단위

    private Long price;                    // 판매 가격

    private Long originalPrice;            // 정가

    private Integer discountRate;          // 할인율

    private String unitPrice;              // 단위 가격

    private String shippingInfo;           // 배송 정보

    private Boolean rocketDelivery;        // 로켓배송 여부

    private Integer stock;                 // 재고

    private String description;            // 상품 설명

    private List<String> detailImages;     // 상세 설명 이미지

    private Long categoryId;               // 카테고리 ID

    private Boolean isWished;              // 찜 여부

    private Boolean canWriteReview;        // 리뷰 작성 가능 여부

    private ReviewSummaryResponse reviewSummary; // 리뷰 요약

}