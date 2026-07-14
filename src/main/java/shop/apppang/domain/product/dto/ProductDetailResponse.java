package shop.apppang.domain.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import shop.apppang.domain.review.dto.ReviewSummaryResponse;

import java.util.List;

@Getter
@AllArgsConstructor
public class ProductDetailResponse {

    private Long productId;

    private String brand;

    private String name;

    private List<String> images;

    private String optionInfo;

    private Long price;

    private Long originalPrice;

    private Integer discountRate;

    private String unitPrice;

    private String shippingInfo;

    private Boolean rocketDelivery;

    private Integer stock;

    private String description;

    private List<String> detailImages;

    private Long categoryId;

    private Boolean isWished;

    private Boolean canWriteReview;

    private ReviewSummaryResponse reviewSummary;

}