package shop.apppang.domain.product.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import shop.apppang.domain.category.entity.CategoryEntity;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "products")
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryEntity category;

    @Column(nullable = true, length = 100)
    private String brand;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false)
    private Long price;

    @Column(nullable = true)
    private Long originalPrice;

    @Column(nullable = true)
    private Integer stock;

    @Column(nullable = true, length = 255)
    private String optionInfo;

    @Column(nullable = true, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = true, columnDefinition = "TEXT")
    private String detailContent;

    @Column(nullable = true, length = 255)
    private String shippingInfo;

    @Column(nullable = true)
    private Boolean rocketDelivery;

    @Column(nullable = true)
    private Boolean isActive;

    @Column(nullable = true, length = 255)
    private String unitPrice;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public ProductEntity(CategoryEntity category, String brand, String name,String unitPrice, Long price, Long originalPrice, Integer stock, String optionInfo, String description, String detailContent, String shippingInfo, Boolean rocketDelivery, Boolean isActive) {
        this.category = category;
        this.brand = brand;
        this.name = name;
        this.price = price;
        this.unitPrice = unitPrice;
        this.originalPrice = originalPrice;
        this.stock = stock;
        this.optionInfo = optionInfo;
        this.description = description;
        this.detailContent = detailContent;
        this.shippingInfo = shippingInfo;
        this.rocketDelivery = rocketDelivery;
        this.isActive = isActive;
    }
    public void decreaseStock(int amount) { this.stock -= amount; }
    public void increaseStock(int amount) { this.stock += amount; }
}
