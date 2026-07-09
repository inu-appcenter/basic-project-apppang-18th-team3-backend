package kr.inuappcenter.apppang.domain.address.service;

import kr.inuappcenter.apppang.domain.address.dto.AddressRequest;
import kr.inuappcenter.apppang.domain.address.dto.AddressResponse;
import kr.inuappcenter.apppang.domain.address.entity.Address;
import kr.inuappcenter.apppang.domain.address.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor        // final 필드를 자동으로 생성자 주입
public class AddressService {

    private final AddressRepository addressRepository;

    // ① 목록 조회
    @Transactional(readOnly = true)
    public List<AddressResponse> getAddresses(Long userId) {
        return addressRepository.findByUserId(userId).stream()
                .map(AddressResponse::from)   // 각 엔티티를 응답 DTO로 변환
                .toList();
    }

    // ② 추가
    @Transactional
    public AddressResponse addAddress(Long userId, AddressRequest req) {
        if (Boolean.TRUE.equals(req.isDefault())) {
            unsetOtherDefaults(userId);       // 새 기본배송지면 기존 기본 해제
        }
        Address address = Address.builder()
                .userId(userId)
                .recipientName(req.recipientName())
                .phone(req.phone())
                .zipcode(req.zipcode())
                .address(req.address())
                .detailAddress(req.detailAddress())
                .normalDeliveryRequest(req.normalDeliveryRequest())
                .rocketDeliveryRequest(req.rocketDeliveryRequest())
                .isDefault(Boolean.TRUE.equals(req.isDefault()))
                .build();
        return AddressResponse.from(addressRepository.save(address));
    }

    // ③ 수정
    @Transactional
    public AddressResponse updateAddress(Long userId, Long addressId, AddressRequest req) {
        Address address = findMyAddress(userId, addressId);
        if (Boolean.TRUE.equals(req.isDefault())) {
            unsetOtherDefaults(userId);
        }
        address.update(req.recipientName(), req.phone(), req.zipcode(),
                req.address(), req.detailAddress(),
                req.normalDeliveryRequest(), req.rocketDeliveryRequest(), req.isDefault());
        return AddressResponse.from(address);  // 변경 감지로 자동 UPDATE
    }

    // ④ 삭제
    @Transactional
    public void deleteAddress(Long userId, Long addressId) {
        Address address = findMyAddress(userId, addressId);
        addressRepository.delete(address);
    }

    // --- 내부 헬퍼 ---
    private Address findMyAddress(Long userId, Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new IllegalArgumentException("배송지를 찾을 수 없습니다"));
        if (!address.getUserId().equals(userId)) {   // 본인 배송지인지 확인
            throw new IllegalArgumentException("본인의 배송지만 접근할 수 있습니다");
        }
        return address;
    }

    private void unsetOtherDefaults(Long userId) {
        addressRepository.findByUserIdAndIsDefaultTrue(userId)
                .forEach(Address::unsetDefault);
    }
}