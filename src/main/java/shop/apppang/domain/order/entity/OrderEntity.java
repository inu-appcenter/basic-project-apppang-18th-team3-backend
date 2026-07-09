package shop.apppang.domain.order.entity;

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
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "orders")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(nullable = true, length = 100)
    private String shippingRecipientName;

    @Column(nullable = true, length = 20)
    private String shippingPhone;

    @Column(nullable = true, length = 10)
    private String shippingZipcode;

    @Column(nullable = true, length = 255)
    private String shippingAddress;

    @Column(nullable = true, length = 255)
    private String shippingDetailAddress;

    @Column(nullable = true, length = 255)
    private String deliveryRequest;

    @Column(nullable = true)
    private Long productAmount;

    @Column(nullable = true)
    private Long discountAmount;

    @Column(nullable = true)
    private Long shippingFee;

    @Column(nullable = true, length = 50)
    private String paymentMethod;

    @Column(nullable = true)
    private Long totalPrice;

    @Column(nullable = true, length = 50)
    private String status;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public OrderEntity(UserEntity user, String shippingRecipientName, String shippingPhone,
                       String shippingZipcode, String shippingAddress, String shippingDetailAddress,
                       String deliveryRequest, Long productAmount, Long discountAmount,
                       Long shippingFee, String paymentMethod, Long totalPrice, String status) {
        this.user = user;
        this.shippingRecipientName = shippingRecipientName;
        this.shippingPhone = shippingPhone;
        this.shippingZipcode = shippingZipcode;
        this.shippingAddress = shippingAddress;
        this.shippingDetailAddress = shippingDetailAddress;
        this.deliveryRequest = deliveryRequest;
        this.productAmount = productAmount;
        this.discountAmount = discountAmount;
        this.shippingFee = shippingFee;
        this.paymentMethod = paymentMethod;
        this.totalPrice = totalPrice;
        this.status = status;
    }
}