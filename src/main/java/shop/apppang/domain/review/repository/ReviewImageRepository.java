package shop.apppang.domain.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.apppang.domain.review.entity.ReviewEntity;
import shop.apppang.domain.review.entity.ReviewImageEntity;

import java.util.List;

public interface ReviewImageRepository extends JpaRepository<ReviewImageEntity, Long> {

    List<ReviewImageEntity> findByReview(ReviewEntity review);

}