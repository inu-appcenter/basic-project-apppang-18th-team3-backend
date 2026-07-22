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
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "products")
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryEntity category;

    @Column(length = 100)
    private String brand;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false)
    private Long price;

    private Long originalPrice;

    private Integer stock;

    @Column(length = 255)
    private String optionInfo;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String detailContent;

    @Column(length = 255)
    private String shippingInfo;

    private Boolean rocketDelivery;

    private Boolean isActive;

    @Column(length = 255)
    private String unitPrice;

    @Column(nullable = false)
    private Long salesCount;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public ProductEntity(
            CategoryEntity category,
            String brand,
            String name,
            String unitPrice,
            Long price,
            Long originalPrice,
            Integer stock,
            String optionInfo,
            String description,
            String detailContent,
            String shippingInfo,
            Boolean rocketDelivery,
            Boolean isActive,
            Long salesCount
    ) {
        this.category = category;
        this.brand = brand;
        this.name = name;
        this.unitPrice = unitPrice;
        this.price = price;
        this.originalPrice = originalPrice;
        this.stock = stock;
        this.optionInfo = optionInfo;
        this.description = description;
        this.detailContent = detailContent;
        this.shippingInfo = shippingInfo;
        this.rocketDelivery = rocketDelivery;
        this.isActive = isActive;
        this.salesCount = (salesCount != null) ? salesCount : 0L;
    }

    public void decreaseStock(int amount) {
        this.stock -= amount;
    }

    public void increaseStock(int amount) {
        this.stock += amount;
    }
}