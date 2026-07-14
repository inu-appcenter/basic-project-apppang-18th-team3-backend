package shop.apppang.domain.cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.apppang.domain.cart.entity.CartItemEntity;
import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<CartItemEntity, Long> {
    List<CartItemEntity> findByUser_Id(Long userId);
    Optional<CartItemEntity> findByUser_IdAndProduct_Id(Long userId, Long productId);  // 담기 합산용
}