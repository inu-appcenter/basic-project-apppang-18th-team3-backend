package shop.apppang.domain.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.apppang.domain.order.entity.OrderItemEntity;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Long> {
    List<OrderItemEntity> findByOrder_Id(Long orderId);

    // 여러 주문의 주문상세를 한 번의 쿼리로 조회한다 (주문 목록의 N+1 방지용).
    // 주문마다 findByOrder_Id를 반복 호출하면 주문 N개당 쿼리가 N번 나가는데,
    // IN 절로 묶으면 주문상세 조회가 딱 1번으로 끝난다.
    List<OrderItemEntity> findByOrder_IdIn(List<Long> orderIds);
}