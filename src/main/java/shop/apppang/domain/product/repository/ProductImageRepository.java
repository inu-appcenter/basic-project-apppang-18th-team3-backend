package shop.apppang.domain.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.apppang.domain.product.entity.ProductEntity;
import shop.apppang.domain.product.entity.ProductImageEntity;

import java.util.List;
import java.util.Optional;

public interface ProductImageRepository extends JpaRepository<ProductImageEntity, Long> {

    // 대표 이미지 조회 (상품 목록에서 사용)
    Optional<ProductImageEntity> findByProductAndIsMainTrue(ProductEntity product);

    // 상품의 모든 이미지 조회 (상품 상세에서 사용)
    List<ProductImageEntity> findByProduct(ProductEntity product);
  
    List<ProductImageEntity> findByProductIdInAndIsMainTrue(List<Long> productIds);

}
