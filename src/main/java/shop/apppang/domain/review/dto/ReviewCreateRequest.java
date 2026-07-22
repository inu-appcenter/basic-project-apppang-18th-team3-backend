package shop.apppang.domain.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ReviewCreateRequest {

    @Schema(description = "별점 (1~5)", example = "5")
    private Integer rating;

    @Schema(description = "리뷰 제목", example = "재구매 의사 있어요")
    private String title;

    @Schema(description = "리뷰 내용", example = "배송 빠르고 좋아요")
    private String content;

    @Schema(description = "리뷰 이미지 URL 목록")
    private List<String> images;

}