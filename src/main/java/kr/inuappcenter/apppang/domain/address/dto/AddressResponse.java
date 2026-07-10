package kr.inuappcenter.apppang.domain.address.dto;

import kr.inuappcenter.apppang.domain.address.entity.Address;

public record AddressResponse(
        Long addressId,
        String recipientName,
        String phone,
        String zipcode,
        String address,
        String detailAddress,
        String normalDeliveryRequest,
        String rocketDeliveryRequest,
        Boolean isDefault
) {
    // 엔티티 → 응답 DTO 변환
    public static AddressResponse from(Address a) {
        return new AddressResponse(
                a.getId(), a.getRecipientName(), a.getPhone(), a.getZipcode(),
                a.getAddress(), a.getDetailAddress(),
                a.getNormalDeliveryRequest(), a.getRocketDeliveryRequest(), a.getIsDefault()
        );
    }
}