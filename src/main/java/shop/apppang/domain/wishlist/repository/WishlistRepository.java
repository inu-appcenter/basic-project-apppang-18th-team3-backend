package shop.apppang.domain.wishlist.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.apppang.domain.wishlist.entity.WishlistEntity;
import java.util.List;
import java.util.Optional;

public interface WishlistRepository extends JpaRepository<WishlistEntity, Long> {
    List<WishlistEntity> findByUser_Id(Long userId);
    boolean existsByUser_IdAndProduct_Id(Long userId, Long productId);
    Optional<WishlistEntity> findByUser_IdAndProduct_Id(Long userId, Long productId);
}