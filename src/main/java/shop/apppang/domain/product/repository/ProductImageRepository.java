package shop.apppang.domain.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.apppang.domain.product.entity.ProductImageEntity;

import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImageEntity, Long> {

    List<ProductImageEntity> findByProductIdInAndIsMainTrue(List<Long> productIds);

    List<ProductImageEntity> findByProduct(ProductEntity product);

}
