package shop.apppang.domain.address.dto;

import shop.apppang.domain.address.entity.AddressEntity;

public record AddressResponse(
        Long addressId,
        String recipientName,
        String phone,
        String zipcode,
        String address,
        String detailAddress,
        String normalDeliveryRequest,
        String rocketDeliveryRequest,
        Boolean isDefaultAddress
) {
    // 엔티티 → 응답 DTO 변환
    public static AddressResponse from(AddressEntity a) {
        return new AddressResponse(
                a.getId(), a.getRecipientName(), a.getPhone(), a.getZipcode(),
                a.getAddress(), a.getDetailAddress(),
                a.getNormalDeliveryRequest(), a.getRocketDeliveryRequest(), a.getIsDefaultAddress()
        );
    }
}