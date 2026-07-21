package shop.apppang.domain.review.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import shop.apppang.domain.review.entity.ReviewEntity;

public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {


    // 상품 리뷰 목록
    Page<ReviewEntity> findByProductId(
            Long productId,
            Pageable pageable
    );

    // 상품 평균 별점
    @Query("""
            SELECT AVG(r.rating)
            FROM ReviewEntity r
            WHERE r.product.id = :productId
            """)
    Double findAverageRatingByProductId(Long productId);

    // 상품 리뷰 개수
    long countByProductId(Long productId);

}