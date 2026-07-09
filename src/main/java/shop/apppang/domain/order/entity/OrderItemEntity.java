package shop.apppang.domain.order.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.apppang.domain.product.entity.ProductEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "order_items")
public class OrderItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    @Column(nullable = true)
    private Integer quantity;

    @Column(nullable = true)
    private Long price;

    @Column(nullable = true, length = 50)
    private String status;

    @Builder
    public OrderItemEntity(OrderEntity order, ProductEntity product, Integer quantity,
                           Long price, String status) {
        this.order = order;
        this.product = product;
        this.quantity = quantity;
        this.price = price;
        this.status = status;
    }
}