package shop.apppang.domain.search.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.apppang.domain.search.entity.SearchHistory;
import shop.apppang.domain.user.entity.User;

import java.util.List;
import java.util.Optional;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {

    List<SearchHistory> findByUserOrderBySearchedAtAsc(User user);

    List<SearchHistory> findByUserOrderBySearchedAtDesc(User user);

    Optional<SearchHistory> findTopByUserOrderBySearchedAtDesc(User user);

    long deleteByUserAndKeyword(User user, String keyword);

}
