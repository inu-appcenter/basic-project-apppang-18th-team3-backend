package shop.apppang.domain.review.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ReviewCreateRequest {

    private Integer rating;

    private String title;

    private String content;

    private List<String> images;

}