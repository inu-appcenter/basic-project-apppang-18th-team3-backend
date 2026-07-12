package shop.apppang.domain.search.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shop.apppang.domain.product.entity.ProductEntity;
import shop.apppang.domain.product.repository.ProductRepository;
import shop.apppang.domain.search.dto.SearchSuggestionResponse;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {

    private static final int MIN_KEYWORD_LENGTH = 2;

    private final ProductRepository productRepository;

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

}
