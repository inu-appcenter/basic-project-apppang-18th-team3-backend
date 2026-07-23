package shop.apppang.domain.product.dto;

import java.util.List;

public record ProductListResponse(
        Long categoryId,

        String categoryName,

        String keyword,

        Integer page,

        Integer size,

        Integer total,

        Boolean hasNext,

        List<ProductItemResponse> items
) {}