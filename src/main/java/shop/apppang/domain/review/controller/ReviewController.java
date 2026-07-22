package shop.apppang.domain.review.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    // 리뷰 작성 (텍스트 + 이미지 파일을 한 번에)
    @PostMapping(value = "/{productId}/reviews", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ReviewCreateResponse> createReview(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long productId,
            @ModelAttribute ReviewCreateRequest request
    ) {
        ReviewCreateResponse response = reviewService.createReview(userId, productId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 리뷰 목록 조회 (그대로)
    @GetMapping("/{productId}/reviews")
    public ResponseEntity<ReviewListResponse> getReviews(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(reviewService.getReviews(productId, page, size));
    }
}