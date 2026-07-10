package kr.inuappcenter.apppang.domain.address.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity                         // 이 클래스가 DB 테이블과 연결됨을 표시
@Table(name = "addresses")      // 매핑될 테이블 이름
@Getter                         // Lombok: 모든 필드의 getter 자동 생성
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // JPA가 요구하는 기본 생성자
@AllArgsConstructor
@Builder                        // 객체를 편하게 만드는 빌더 패턴
public class Address {

    @Id                                              // 기본키(PK)
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // DB가 자동 증가(AUTO_INCREMENT)
    @Column(name = "address_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;             // FK지만 지금은 단순 Long으로 저장 (아래 설명)

    private String recipientName;    // camelCase → DB는 recipient_name 으로 자동 매핑
    private String phone;
    private String zipcode;
    private String address;
    private String detailAddress;
    private String normalDeliveryRequest;
    private String rocketDeliveryRequest;
    private Boolean isDefault;
    private LocalDateTime createdAt;

    @PrePersist                      // 저장 직전에 자동 실행
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.isDefault == null) this.isDefault = false;
    }

    // 수정용 메서드 (넘어온 값만 바꿈 = PATCH)
    public void update(String recipientName, String phone, String zipcode,
                       String address, String detailAddress,
                       String normalDeliveryRequest, String rocketDeliveryRequest,
                       Boolean isDefault) {
        if (recipientName != null) this.recipientName = recipientName;
        if (phone != null) this.phone = phone;
        if (zipcode != null) this.zipcode = zipcode;
        if (address != null) this.address = address;
        if (detailAddress != null) this.detailAddress = detailAddress;
        if (normalDeliveryRequest != null) this.normalDeliveryRequest = normalDeliveryRequest;
        if (rocketDeliveryRequest != null) this.rocketDeliveryRequest = rocketDeliveryRequest;
        if (isDefault != null) this.isDefault = isDefault;
    }

    public void unsetDefault() { this.isDefault = false; }  // 기본배송지 해제용
}