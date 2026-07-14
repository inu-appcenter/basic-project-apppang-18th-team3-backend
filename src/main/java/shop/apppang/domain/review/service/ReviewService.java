package shop.apppang.domain.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.apppang.domain.product.entity.ProductEntity;
import shop.apppang.domain.product.repository.ProductRepository;
import shop.apppang.domain.review.dto.ReviewCreateRequest;
import shop.apppang.domain.review.dto.ReviewCreateResponse;
import shop.apppang.domain.review.dto.ReviewItemResponse;
import shop.apppang.domain.review.dto.ReviewListResponse;
import shop.apppang.domain.review.entity.ReviewEntity;
import shop.apppang.domain.review.entity.ReviewImageEntity;
import shop.apppang.domain.review.repository.ReviewImageRepository;
import shop.apppang.domain.review.repository.ReviewRepository;
import shop.apppang.domain.user.entity.User;
import shop.apppang.domain.user.repository.UserRepository;

import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;

    private final ReviewImageRepository reviewImageRepository;

    private final ProductRepository productRepository;

    private final UserRepository userRepository;



    // 리뷰 작성
    public ReviewCreateResponse createReview(
            Long userId,
            Long productId,
            ReviewCreateRequest request
    ){

        // 주문 도메인 연결 후 추가 (배송 완료된 상품인지 확인)


        User user =
                userRepository.findById(userId)
                        .orElseThrow(() ->
                                new IllegalArgumentException("사용자를 찾을 수 없습니다")
                        );


        ProductEntity product =
                productRepository.findById(productId)
                        .orElseThrow(() ->
                                new IllegalArgumentException("상품을 찾을 수 없습니다")
                        );



        ReviewEntity review =
                ReviewEntity.builder()
                        .user(user)
                        .product(product)
                        .rating(request.getRating())
                        .title(request.getTitle())
                        .content(request.getContent())
                        .build();


        ReviewEntity savedReview =
                reviewRepository.save(review);



        // 리뷰 이미지 저장
        if(request.getImages() != null){

            request.getImages()
                    .forEach(imageUrl -> {

                        ReviewImageEntity image =
                                ReviewImageEntity.builder()
                                        .review(savedReview)
                                        .imageUrl(imageUrl)
                                        .build();


                        reviewImageRepository.save(image);

                    });

        }



        return new ReviewCreateResponse(
                savedReview.getId(),
                savedReview.getRating(),
                savedReview.getTitle()
        );

    }

    // 리뷰 목록 조회
    @Transactional(readOnly = true)
    public ReviewListResponse getReviews(
            Long productId,
            int page,
            int size
    ){

        ProductEntity product =
                productRepository.findById(productId)
                        .orElseThrow(() ->
                                new IllegalArgumentException("상품을 찾을 수 없습니다")
                        );


        PageRequest pageable =
                PageRequest.of(page - 1, size);



        Page<ReviewEntity> reviews =
                reviewRepository.findByProductId(
                        productId,
                        pageable
                );



        Double averageRating =
                reviewRepository.findAverageRatingByProductId(productId);



        Long reviewCount =
                reviewRepository.countByProductId(productId);



        List<ReviewItemResponse> items =
                reviews.stream()
                        .map(review -> {


                            List<String> images =
                                    reviewImageRepository.findByReview(review)
                                            .stream()
                                            .map(ReviewImageEntity::getImageUrl)
                                            .toList();



                            return new ReviewItemResponse(
                                    review.getId(),
                                    review.getUser().getName(),
                                    review.getRating(),
                                    review.getTitle(),
                                    review.getContent(),
                                    review.getCreatedAt(),
                                    images
                            );

                        })
                        .toList();



        return new ReviewListResponse(
                product.getName(),
                averageRating,
                reviewCount,
                page,
                reviews.getTotalElements(),
                items
        );

    }

}