package kr.inuappcenter.apppang.domain.address.dto;

public record AddressRequest(
        String recipientName,
        String phone,
        String zipcode,
        String address,
        String detailAddress,
        String normalDeliveryRequest,
        String rocketDeliveryRequest,
        Boolean isDefault
) {}