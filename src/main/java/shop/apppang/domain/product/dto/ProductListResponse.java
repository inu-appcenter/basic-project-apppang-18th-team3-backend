package shop.apppang.domain.product.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record ProductListResponse(
        @JsonProperty("category_id")
        Long categoryId,

        @JsonProperty("category_name")
        String categoryName,

        String keyword,

        Integer page,

        Integer size,

        Integer total,

        @JsonProperty("has_next")
        Boolean hasNext,

        List<ProductItemResponse> items
) {}