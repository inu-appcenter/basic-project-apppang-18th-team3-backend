package shop.apppang.domain.product.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import shop.apppang.domain.review.dto.ReviewSummaryResponse;

import java.util.List;

@Getter
@AllArgsConstructor
public class ProductDetailResponse {

    @JsonProperty("product_id")
    private Long productId;

    private String brand;

    private String name;

    private List<String> images;

    @JsonProperty("option_info")
    private String optionInfo;

    private Long price;

    @JsonProperty("original_price")
    private Long originalPrice;

    @JsonProperty("discount_rate")
    private Integer discountRate;

    @JsonProperty("unit_price")
    private String unitPrice;

    @JsonProperty("shipping_info")
    private String shippingInfo;

    @JsonProperty("rocket_delivery")
    private Boolean rocketDelivery;

    private Integer stock;

    private String description;

    @JsonProperty("detail_images")
    private List<String> detailImages;

    @JsonProperty("category_id")
    private Long categoryId;

    @JsonProperty("is_wished")
    private Boolean isWished;

    @JsonProperty("can_write_review")
    private Boolean canWriteReview;

    @JsonProperty("review_summary")
    private ReviewSummaryResponse reviewSummary;

}