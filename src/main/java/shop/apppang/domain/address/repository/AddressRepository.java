package shop.apppang.domain.address.repository;

import shop.apppang.domain.address.entity.AddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AddressRepository extends JpaRepository<AddressEntity, Long> {
    List<AddressEntity> findByUserId(Long userId);              // 특정 회원의 배송지 목록
    List<AddressEntity> findByUserIdAndIsDefaultAddressTrue(Long userId); // 기본배송지 찾기
}