package shop.apppang.domain.search.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.apppang.domain.auth.exception.MemberNotFoundException;
import shop.apppang.domain.product.entity.ProductEntity;
import shop.apppang.domain.product.repository.ProductRepository;
import shop.apppang.domain.search.dto.SearchHistoryListResponse;
import shop.apppang.domain.search.dto.SearchSuggestionResponse;
import shop.apppang.domain.search.entity.SearchHistory;
import shop.apppang.domain.search.repository.SearchHistoryRepository;
import shop.apppang.domain.user.entity.User;
import shop.apppang.domain.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {

    private static final int MIN_KEYWORD_LENGTH = 2;

    private static final int MAX_HISTORY_SIZE = 10;

    private final ProductRepository productRepository;

    private final SearchHistoryRepository searchHistoryRepository;

    private final UserRepository userRepository;

    public SearchSuggestionResponse getSuggestions(String keyword) {

        String trimmedKeyword = (keyword == null) ? "" : keyword.trim();

        if (trimmedKeyword.length() < MIN_KEYWORD_LENGTH) {
            return new SearchSuggestionResponse(List.of());
        }

        List<ProductEntity> products = productRepository
                .findTop10ByNameStartingWithIgnoreCaseAndIsActiveTrueOrderByIdDesc(trimmedKeyword);

        List<String> suggestions = products.stream()
                .map(ProductEntity::getName)
                .toList();

        return new SearchSuggestionResponse(suggestions);
    }

    @Transactional
    public void saveHistory(Long userId, String keyword) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new MemberNotFoundException("사용자를 찾을 수 없습니다."));

        searchHistoryRepository.save(SearchHistory.builder()
                .user(user)
                .keyword(keyword)
                .build());

        List<SearchHistory> histories = searchHistoryRepository.findByUserOrderBySearchedAtAsc(user);

        if (histories.size() > MAX_HISTORY_SIZE) {
            List<SearchHistory> overflow = histories.subList(0, histories.size() - MAX_HISTORY_SIZE);
            searchHistoryRepository.deleteAll(overflow);
        }
    }

    @Transactional
    public void deleteHistory(Long userId, String keyword) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new MemberNotFoundException("사용자를 찾을 수 없습니다."));

        searchHistoryRepository.deleteByUserAndKeyword(user, keyword);
    }

    @Transactional(readOnly = true)
    public SearchHistoryListResponse getHistories(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new MemberNotFoundException("사용자를 찾을 수 없습니다."));

        List<SearchHistoryListResponse.Item> items = searchHistoryRepository.findByUserOrderBySearchedAtDesc(user)
                .stream()
                .map(history -> new SearchHistoryListResponse.Item(history.getKeyword(), history.getSearchedAt()))
                .toList();

        return new SearchHistoryListResponse(items);
    }

}
