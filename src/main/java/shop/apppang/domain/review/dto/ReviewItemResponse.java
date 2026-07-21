package shop.apppang.domain.review.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class ReviewItemResponse {

    private Long reviewId;

    private String userName;

    private Integer rating;

    private String title;

    private String content;

    private LocalDateTime createdAt;

    private List<String> images;

}