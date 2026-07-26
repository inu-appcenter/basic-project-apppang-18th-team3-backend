package shop.apppang.domain.address.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import shop.apppang.domain.address.dto.AddressRequest;
import shop.apppang.domain.address.dto.AddressResponse;
import shop.apppang.domain.address.service.AddressService;
import shop.apppang.global.exception.ErrorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name = "배송지")
@RestController                         // 이 클래스가 REST API임을 표시
@RequestMapping("/api/addresses")       // 공통 경로
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    // 배송지 목록 조회  →  GET /api/addresses?userId=1
    @Operation(summary = "내 배송지 목록")
    @ApiResponse(responseCode = "200", description = "배송지 목록 (없으면 빈 배열)",
            content = @Content(schema = @Schema(implementation = AddressResponse.class),
                    examples = @ExampleObject(value = """
                            [
                              {
                                "addressId": 3,
                                "recipientName": "고명재",
                                "phone": "01012345678",
                                "zipcode": "06241",
                                "address": "서울시 강남구 ...",
                                "detailAddress": "101동 202호",
                                "normalDeliveryRequest": "문 앞에 놓아주세요",
                                "rocketDeliveryRequest": "경비실에 맡겨주세요",
                                "isDefaultAddress": true
                              }
                            ]
                            """)))
    @GetMapping
    public ResponseEntity<List<AddressResponse>> getAddresses(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(addressService.getAddresses(userId));
    }

    // 배송지 추가  →  POST /api/addresses?userId=1  (body: 배송지 정보)
    @Operation(summary = "배송지 추가")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "배송지 추가 성공",
                    content = @Content(schema = @Schema(implementation = AddressResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "addressId": 3,
                                      "recipientName": "고명재",
                                      "phone": "01012345678",
                                      "zipcode": "06241",
                                      "address": "서울시 강남구 ...",
                                      "detailAddress": "101동 202호",
                                      "normalDeliveryRequest": "문 앞에 놓아주세요",
                                      "rocketDeliveryRequest": "경비실에 맡겨주세요",
                                      "isDefaultAddress": true
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "필수 정보 누락 / 휴대폰 번호 형식 오류 중 하나",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"error\": \"필수 정보를 입력해주세요\"}")))
    })
    @PostMapping
    public ResponseEntity<AddressResponse> addAddress(
            @AuthenticationPrincipal Long userId,
            @RequestBody AddressRequest request) {
        return ResponseEntity.status(201).body(addressService.addAddress(userId, request));
    }

    // 배송지 수정  →  PATCH /api/addresses/3?userId=1
    @Operation(summary = "배송지 수정")
    @ApiResponse(responseCode = "200", description = "배송지 수정 성공",
            content = @Content(schema = @Schema(implementation = AddressResponse.class),
                    examples = @ExampleObject(value = """
                            {
                              "addressId": 3,
                              "recipientName": "고명재",
                              "phone": "01012345678",
                              "zipcode": "06241",
                              "address": "서울시 강남구 ...",
                              "detailAddress": "303호",
                              "normalDeliveryRequest": "직접 받을게요",
                              "rocketDeliveryRequest": "경비실에 맡겨주세요",
                              "isDefaultAddress": true
                            }
                            """)))
    @PatchMapping("/{addressId}")
    public ResponseEntity<AddressResponse> updateAddress(
            @AuthenticationPrincipal Long userId,
            @Parameter(description = "배송지 ID", required = true) @PathVariable Long addressId,
            @RequestBody AddressRequest request) {
        return ResponseEntity.ok(addressService.updateAddress(userId, addressId, request));
    }

    // 배송지 삭제  →  DELETE /api/addresses/3?userId=1
    @Operation(summary = "배송지 삭제")
    @ApiResponse(responseCode = "204", description = "배송지 삭제 성공 (본문 없음)")
    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteAddress(
            @AuthenticationPrincipal Long userId,
            @Parameter(description = "배송지 ID", required = true) @PathVariable Long addressId) {
        addressService.deleteAddress(userId, addressId);
        return ResponseEntity.noContent().build();   // 204
    }
}