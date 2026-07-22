package shop.apppang.domain.review.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
    @Operation(summary = "리뷰 작성 (구매자만)")
    @PostMapping("/{productId}/reviews")
    public ResponseEntity<ReviewCreateResponse> createReview(
            @Parameter(description = "상품 ID", required = true) @PathVariable Long productId,
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
    @Operation(summary = "상품 리뷰 목록 조회")
    @GetMapping("/{productId}/reviews")
    public ResponseEntity<ReviewListResponse> getReviews(
            @Parameter(description = "상품 ID", required = true) @PathVariable Long productId,
            @Parameter(description = "페이지 번호", example = "1") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "페이지당 개수", example = "10") @RequestParam(defaultValue = "10") int size
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