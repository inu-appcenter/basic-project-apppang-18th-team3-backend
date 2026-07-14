package shop.apppang.domain.review.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ReviewListResponse {

    private String productName;

    private Double averageRating;

    private Long reviewCount;

    private Integer page;

    private Long total;

    private List<ReviewItemResponse> items;

}