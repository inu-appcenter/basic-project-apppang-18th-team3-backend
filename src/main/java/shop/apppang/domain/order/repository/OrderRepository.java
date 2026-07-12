package shop.apppang.domain.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.apppang.domain.order.entity.OrderEntity;
import java.util.List;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    List<OrderEntity> findByUser_IdOrderByCreatedAtDesc(Long userId);
}