    package shop.apppang.domain.product.entity;

    import jakarta.persistence.*;
    import lombok.AccessLevel;
    import lombok.Builder;
    import lombok.Getter;
    import lombok.NoArgsConstructor;

    @Entity
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Table(name = "product_images")
    public class ProductImageEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne
        @JoinColumn(name = "product_id", nullable = false)
        private ProductEntity product;

        @Column(nullable = true, length = 255)
        private String imageUrl;

        @Column(nullable = true)
        private Integer displayOrder;

        @Column(nullable = true)
        private Boolean isMain;

        @Builder
        public ProductImageEntity(ProductEntity product, String imageUrl, Integer displayOrder, Boolean isMain) {
            this.product = product;
            this.imageUrl = imageUrl;
            this.displayOrder = displayOrder;
            this.isMain = isMain;
        }
    }
