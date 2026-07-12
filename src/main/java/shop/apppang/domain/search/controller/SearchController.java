package shop.apppang.domain.search.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import shop.apppang.domain.search.dto.SearchHistoryRequest;
import shop.apppang.domain.search.dto.SearchSuggestionResponse;
import shop.apppang.domain.search.service.SearchService;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping("/search/suggestions")
    public SearchSuggestionResponse getSuggestions(@RequestParam(required = false, defaultValue = "") String keyword) {

        return searchService.getSuggestions(keyword);

    }

    @PostMapping("/search/history")
    public ResponseEntity<Void> saveHistory(@AuthenticationPrincipal Long userId,
                                             @Valid @RequestBody SearchHistoryRequest request) {

        searchService.saveHistory(userId, request.getKeyword());

        return ResponseEntity.noContent().build();

    }

}
