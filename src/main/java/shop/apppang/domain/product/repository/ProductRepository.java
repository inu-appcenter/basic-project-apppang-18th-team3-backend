package shop.apppang.domain.product.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shop.apppang.domain.product.entity.ProductEntity;

import java.util.List;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    List<ProductEntity> findTop10ByNameStartingWithIgnoreCaseAndIsActiveTrueOrderByIdDesc(String keyword);

    // 추천 상품 검색용
    List<ProductEntity> findTop20ByNameContainingIgnoreCaseAndIsActiveTrueOrderByIdAsc(String keyword);

    // 인기 상품 조회용
    List<ProductEntity> findTop20ByIsActiveTrueOrderBySalesCountDesc();

    @Query("SELECT p FROM ProductEntity p WHERE " +
            "(:categoryId IS NULL OR p.category.id = :categoryId) AND " +
            "(:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(p.isActive = true)")
    Page<ProductEntity> findProductsWithFilters(
            @Param("categoryId") Long categoryId,
            @Param("keyword") String keyword,
            Pageable pageable
    );

}
