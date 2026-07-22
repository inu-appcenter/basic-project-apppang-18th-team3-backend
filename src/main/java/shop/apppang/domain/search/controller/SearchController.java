package shop.apppang.domain.search.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import shop.apppang.domain.search.dto.SearchHistoryListResponse;
import shop.apppang.domain.search.dto.SearchHistoryRequest;
import shop.apppang.domain.search.dto.SearchSuggestionResponse;
import shop.apppang.domain.search.service.SearchService;

@Validated
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @Operation(summary = "검색어 자동완성")
    @GetMapping("/search/suggestions")
    public SearchSuggestionResponse getSuggestions(
            @Parameter(description = "자동완성 검색어", example = "티")
            @RequestParam(required = false, defaultValue = "") String keyword) {

        return searchService.getSuggestions(keyword);

    }

    @Operation(summary = "검색어 저장")
    @PostMapping("/search/history")
    public ResponseEntity<Void> saveHistory(@AuthenticationPrincipal Long userId,
                                             @Valid @RequestBody SearchHistoryRequest request) {

        searchService.saveHistory(userId, request.getKeyword());

        return ResponseEntity.noContent().build();

    }

    @Operation(summary = "검색어 조회")
    @GetMapping("/search/history")
    public SearchHistoryListResponse getHistories(@AuthenticationPrincipal Long userId) {

        return searchService.getHistories(userId);

    }

    @Operation(summary = "검색어 삭제")
    @DeleteMapping("/search/history")
    public ResponseEntity<Void> deleteHistory(@AuthenticationPrincipal Long userId,
                                               @Parameter(description = "삭제할 검색어", required = true)
                                               @RequestParam @NotBlank(message = "keyword는 빈 값일 수 없습니다.") String keyword) {

        searchService.deleteHistory(userId, keyword);

        return ResponseEntity.noContent().build();

    }

}
