package shop.apppang.domain.review.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReviewCreateResponse {

    private Long reviewId;

    private Integer rating;

    private String title;

}