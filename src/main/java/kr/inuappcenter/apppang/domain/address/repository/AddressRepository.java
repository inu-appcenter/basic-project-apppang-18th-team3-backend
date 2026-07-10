package kr.inuappcenter.apppang.domain.address.repository;

import kr.inuappcenter.apppang.domain.address.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUserId(Long userId);              // 특정 회원의 배송지 목록
    List<Address> findByUserIdAndIsDefaultTrue(Long userId); // 기본배송지 찾기
}