package shop.apppang.domain.address.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import shop.apppang.domain.user.entity.UserEntity;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name="addresses")
public class AddressEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(nullable = false, length = 100)
    private String recipientName;

    @Column(nullable = false, length = 20)
    private String phoneNumber;

    @Column(nullable = false, length = 255)
    private String address;

    @Column(nullable = true, length = 10)
    private String zipcode;

    @Column(nullable = true, length = 255)
    private String detailAddress;

    @Column(nullable = true, length = 255)
    private String normalDeliveryRequest;

    @Column(nullable = true, length = 255)
    private String rocketDeliveryRequest;

    @Column(nullable = true)
    private Boolean isDefault;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public AddressEntity(UserEntity user, String recipientName, String phoneNumber, String address, String zipcode, String detailAddress, String normalDeliveryRequest, String rocketDeliveryRequest, Boolean isDefault) {
        this.user = user;
        this.recipientName = recipientName;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.zipcode = zipcode;
        this.detailAddress = detailAddress;
        this.normalDeliveryRequest = normalDeliveryRequest;
        this.rocketDeliveryRequest = rocketDeliveryRequest;
        this.isDefault = isDefault;
    }
}
