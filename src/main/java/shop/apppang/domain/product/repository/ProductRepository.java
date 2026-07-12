package shop.apppang.domain.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.apppang.domain.product.entity.ProductEntity;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    List<ProductEntity> findTop10ByNameStartingWithIgnoreCaseAndIsActiveTrueOrderByIdDesc(String keyword);

    List<ProductEntity> findTop20ByNameContainingIgnoreCaseAndIsActiveTrueOrderByIdAsc(String keyword);

}
