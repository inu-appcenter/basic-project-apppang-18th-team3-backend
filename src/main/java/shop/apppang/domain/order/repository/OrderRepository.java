package shop.apppang.domain.order.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.apppang.domain.order.entity.OrderEntity;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    // 특정 유저의 주문을 페이지 단위로 조회한다.
    // 정렬(최신순 등)은 Controller에서 만든 Pageable의 Sort에 위임하므로 메서드명에는 넣지 않는다.
    Page<OrderEntity> findByUser_Id(Long userId, Pageable pageable);
}
