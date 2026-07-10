package kr.inuappcenter.apppang.domain.address.controller;

import kr.inuappcenter.apppang.domain.address.dto.AddressRequest;
import kr.inuappcenter.apppang.domain.address.dto.AddressResponse;
import kr.inuappcenter.apppang.domain.address.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController                         // 이 클래스가 REST API임을 표시
@RequestMapping("/api/addresses")       // 공통 경로
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    // 배송지 목록 조회  →  GET /api/addresses?userId=1
    @GetMapping
    public ResponseEntity<List<AddressResponse>> getAddresses(@RequestParam Long userId) {
        return ResponseEntity.ok(addressService.getAddresses(userId));
    }

    // 배송지 추가  →  POST /api/addresses?userId=1  (body: 배송지 정보)
    @PostMapping
    public ResponseEntity<AddressResponse> addAddress(
            @RequestParam Long userId,
            @RequestBody AddressRequest request) {
        return ResponseEntity.status(201).body(addressService.addAddress(userId, request));
    }

    // 배송지 수정  →  PATCH /api/addresses/3?userId=1
    @PatchMapping("/{addressId}")
    public ResponseEntity<AddressResponse> updateAddress(
            @RequestParam Long userId,
            @PathVariable Long addressId,
            @RequestBody AddressRequest request) {
        return ResponseEntity.ok(addressService.updateAddress(userId, addressId, request));
    }

    // 배송지 삭제  →  DELETE /api/addresses/3?userId=1
    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteAddress(
            @RequestParam Long userId,
            @PathVariable Long addressId) {
        addressService.deleteAddress(userId, addressId);
        return ResponseEntity.noContent().build();   // 204
    }
}