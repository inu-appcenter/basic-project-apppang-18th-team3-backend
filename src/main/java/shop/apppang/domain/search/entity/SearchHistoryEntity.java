package shop.apppang.domain.search.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import shop.apppang.domain.user.entity.User;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Table(name = "search_histories")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SearchHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = true, length = 255)
    private String keyword;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime searchedAt;

    @Builder
    public SearchHistoryEntity(User user, String keyword) {
        this.user = user;
        this.keyword = keyword;
    }
}
