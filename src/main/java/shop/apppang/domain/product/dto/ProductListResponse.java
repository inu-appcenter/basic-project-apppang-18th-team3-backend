package shop.apppang.domain.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ProductListResponse {

    private Long categoryId;

    private String categoryName;

    private String keyword;

    private Integer page;

    private Integer size;

    private Integer total;

    private Boolean hasNext;

    private List<ProductItemResponse> items;

}