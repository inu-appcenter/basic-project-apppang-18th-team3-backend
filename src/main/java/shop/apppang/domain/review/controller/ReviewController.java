package shop.apppang.domain.review.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.apppang.domain.review.dto.ReviewCreateRequest;
import shop.apppang.domain.review.dto.ReviewCreateResponse;
import shop.apppang.domain.review.dto.ReviewListResponse;
import shop.apppang.domain.review.service.ReviewService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ReviewController {

    private final ReviewService reviewService;



    // 리뷰 작성
    @PostMapping("/{productId}/reviews")
    public ResponseEntity<ReviewCreateResponse> createReview(
            @PathVariable Long productId,
            @RequestBody ReviewCreateRequest request
    ){

        // TODO Security 적용 후 변경
        Long userId = 1L;


        ReviewCreateResponse response =
                reviewService.createReview(
                        userId,
                        productId,
                        request
                );


        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);

    }





    // 리뷰 목록 조회
    @GetMapping("/{productId}/reviews")
    public ResponseEntity<ReviewListResponse> getReviews(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ){

        ReviewListResponse response =
                reviewService.getReviews(
                        productId,
                        page,
                        size
                );


        return ResponseEntity
                .ok(response);

    }

}