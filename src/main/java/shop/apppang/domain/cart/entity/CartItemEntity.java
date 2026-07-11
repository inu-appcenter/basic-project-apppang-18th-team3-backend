package shop.apppang.domain.cart.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.apppang.domain.product.entity.ProductEntity;
import shop.apppang.domain.user.entity.User;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "cart_items")
public class CartItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    @Column(nullable = true)
    private Integer quantity;

    @Builder
    public CartItemEntity(User user, ProductEntity product, Integer quantity) {
        this.user = user;
        this.product = product;
        this.quantity = quantity;
    }
}
