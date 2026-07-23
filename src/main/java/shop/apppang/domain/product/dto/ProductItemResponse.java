package shop.apppang.domain.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductItemResponse {

    private Long productId;

    private String name;

    private String imageUrl;

    private String optionInfo;

    private Long price;

    private Long originalPrice;

    private Integer discountRate;

    private String unitPrice;

    private Double averageRating;

    private Integer reviewCount;

    private String shippingInfo;

    private Boolean rocketDelivery;

}