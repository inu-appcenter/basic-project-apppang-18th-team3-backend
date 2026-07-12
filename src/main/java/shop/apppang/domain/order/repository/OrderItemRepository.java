package shop.apppang.domain.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.apppang.domain.order.entity.OrderItemEntity;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Long> {
    List<OrderItemEntity> findByOrder_Id(Long orderId);
}