package shop.apppang.domain.address.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record AddressRequest(
        @Schema(description = "수령인 이름", example = "고명재") String recipientName,
        @Schema(description = "연락처", example = "01012345678") String phone,
        @Schema(description = "우편번호", example = "06241") String zipcode,
        @Schema(description = "주소", example = "서울시 강남구 ...") String address,
        @Schema(description = "상세 주소", example = "101동 202호") String detailAddress,
        @Schema(description = "일반배송 요청사항", example = "문 앞에 놓아주세요") String normalDeliveryRequest,
        @Schema(description = "로켓배송 요청사항", example = "경비실에 맡겨주세요") String rocketDeliveryRequest,
        @Schema(description = "기본 배송지 여부", example = "true") Boolean isDefaultAddress
) {}