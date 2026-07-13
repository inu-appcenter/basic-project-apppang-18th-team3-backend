package shop.apppang.domain.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ProductListResponse {

    private Long categoryId;                   // 카테고리 ID

    private String categoryName;               // 카테고리명

    private String keyword;                    // 검색 키워드

    private Integer page;                      // 현재 페이지

    private Integer size;                      // 페이지 크기

    private Integer total;                     // 전체 상품 개수

    private Boolean hasNext;                   // 다음 페이지 존재 여부

    private List<ProductItemResponse> items;   // 상품 목록

}