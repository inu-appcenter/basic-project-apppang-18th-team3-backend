package shop.apppang.domain.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.apppang.domain.product.entity.ProductEntity;

import java.util.List;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    // 검색 자동완성
    List<ProductEntity> findTop10ByNameStartingWithIgnoreCaseAndIsActiveTrueOrderByIdDesc(String keyword);

    // 상품 검색
    List<ProductEntity> findTop20ByNameContainingIgnoreCaseAndIsActiveTrueOrderByIdAsc(String keyword);

    // 인기 상품
    List<ProductEntity> findTop10ByOrderBySalesCountDesc();

}