package shop.apppang.domain.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.apppang.domain.category.entity.CategoryEntity;

import java.util.List;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

    // displayOrder 기준 오름차순 조회
    List<CategoryEntity> findAllByOrderByDisplayOrderAsc();

}