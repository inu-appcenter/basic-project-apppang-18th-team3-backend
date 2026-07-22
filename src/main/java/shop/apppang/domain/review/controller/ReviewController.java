package shop.apppang.domain.review.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
import shop.apppang.global.exception.ErrorResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ReviewController {

    private final ReviewService reviewService;

    // 리뷰 작성 (텍스트 + 이미지 파일을 한 번에)
    @Operation(summary = "리뷰 작성 (구매자만)")
    @ApiResponse(responseCode = "403", description = "구매하지 않은 상품에 대한 리뷰 작성 시도",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"error\": \"구매한 상품만 리뷰를 작성할 수 있습니다\"}")))
    @PostMapping(value = "/{productId}/reviews", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ReviewCreateResponse> createReview(
            @AuthenticationPrincipal Long userId,
            @Parameter(description = "상품 ID", required = true) @PathVariable Long productId,
            @ModelAttribute ReviewCreateRequest request
    ) {
        ReviewCreateResponse response = reviewService.createReview(userId, productId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 리뷰 목록 조회
    @Operation(summary = "상품 리뷰 목록 조회")
    @GetMapping("/{productId}/reviews")
    public ResponseEntity<ReviewListResponse> getReviews(
            @Parameter(description = "상품 ID", required = true) @PathVariable Long productId,
            @Parameter(description = "페이지 번호", example = "1") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "페이지당 개수", example = "10") @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(reviewService.getReviews(productId, page, size));
    }
}