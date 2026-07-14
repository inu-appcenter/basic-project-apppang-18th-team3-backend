package shop.apppang.domain.category.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "categories")
public class CategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = true, length = 255)
    private String iconUrl;

    @Column(nullable = true)
    private Integer displayOrder;

    @Builder
    public CategoryEntity(String name, String iconUrl, Integer displayOrder) {
        this.name = name;
        this.iconUrl = iconUrl;
        this.displayOrder = displayOrder;
    }
}
