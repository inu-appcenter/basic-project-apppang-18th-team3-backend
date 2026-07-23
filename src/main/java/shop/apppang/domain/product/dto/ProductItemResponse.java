package shop.apppang.domain.product.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductItemResponse {

    @JsonProperty("product_id")
    private Long productId;

    private String name;

    @JsonProperty("image_url")
    private String imageUrl;

    @JsonProperty("option_info")
    private String optionInfo;

    private Long price;

    @JsonProperty("original_price")
    private Long originalPrice;

    @JsonProperty("discount_rate")
    private Integer discountRate;

    @JsonProperty("unit_price")
    private String unitPrice;

    @JsonProperty("average_rating")
    private Double averageRating;

    @JsonProperty("review_count")
    private Integer reviewCount;

    @JsonProperty("shipping_info")
    private String shippingInfo;

    @JsonProperty("rocket_delivery")
    private Boolean rocketDelivery;

}