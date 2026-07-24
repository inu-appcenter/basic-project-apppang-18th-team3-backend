package shop.apppang.domain.product.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shop.apppang.domain.product.entity.ProductEntity;

import java.util.List;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    // 1. 검색어 자동완성용 메서드 (Top 10)
    List<ProductEntity> findTop10ByNameStartingWithIgnoreCaseAndIsActiveTrueOrderByIdDesc(String name);

    // 1-1. 최근 찾던 상품용 메서드 (최근 검색어로 관련 활성 상품 Top 20)
    List<ProductEntity> findTop20ByNameContainingIgnoreCaseAndIsActiveTrueOrderByIdAsc(String name);

    // 2. 상품 목록 필터링 및 페이징 조회 (Sort는 Pageable 객체로 자동 처리)
    @Query("SELECT p FROM ProductEntity p " +
            "WHERE (:categoryId IS NULL OR p.category.id = :categoryId) " +
            "AND (:keyword IS NULL OR p.name LIKE CONCAT('%', :keyword, '%')) " +
            "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR p.price <= :maxPrice) " +
            "AND (:rocketDelivery IS NULL OR p.rocketDelivery = :rocketDelivery)")
    Page<ProductEntity> findProductsWithFilters(
            @Param("categoryId") Long categoryId,
            @Param("keyword") String keyword,
            @Param("minPrice") Long minPrice,
            @Param("maxPrice") Long maxPrice,
            @Param("rocketDelivery") Boolean rocketDelivery,
            Pageable pageable
    );
}