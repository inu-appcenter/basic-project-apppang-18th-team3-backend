package shop.apppang.domain.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductItemResponse {

    private Long productId;          // 상품 ID

    private String name;             // 상품명

    private String imageUrl;         // 대표 이미지

    private String optionInfo;       // 상품 단위

    private Long price;              // 판매 가격

    private Long originalPrice;      // 정가

    private Integer discountRate;    // 할인율(서비스에서 계산)

    private String unitPrice;        // 단위 가격

    private Double averageRating;    // 평균 평점

    private Integer reviewCount;     // 리뷰 개수

    private String shippingInfo;     // 배송 정보

    private Boolean rocketDelivery;  // 로켓배송 여부

}