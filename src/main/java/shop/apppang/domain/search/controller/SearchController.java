package shop.apppang.domain.search.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
import shop.apppang.global.exception.ErrorResponse;

@Tag(name = "검색")
@Validated
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @Operation(summary = "검색어 자동완성")
    @ApiResponse(responseCode = "200", description = "자동완성 결과 (매칭 없으면 빈 배열, 있으면 최대 10개)",
            content = @Content(schema = @Schema(implementation = SearchSuggestionResponse.class),
                    examples = @ExampleObject(value = """
                            { "suggestions": ["티셔츠", "티팬티", "티세트"] }
                            """)))
    @GetMapping("/search/suggestions")
    public SearchSuggestionResponse getSuggestions(
            @Parameter(description = "자동완성 검색어", example = "티")
            @RequestParam(required = false, defaultValue = "") String keyword) {

        return searchService.getSuggestions(keyword);

    }

    @Operation(summary = "검색어 저장")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "저장 성공 (본문 없음)"),
            @ApiResponse(responseCode = "400", description = "검색어 누락 / 50자 초과 중 하나",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"error\": \"검색어를 입력해주세요.\"}"))),
            @ApiResponse(responseCode = "401", description = "로그인 필요",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"error\": \"로그인이 필요합니다.\"}")))
    })
    @PostMapping("/search/history")
    public ResponseEntity<Void> saveHistory(@AuthenticationPrincipal Long userId,
                                             @Valid @RequestBody SearchHistoryRequest request) {

        searchService.saveHistory(userId, request.getKeyword());

        return ResponseEntity.noContent().build();

    }

    @Operation(summary = "검색어 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "검색 기록 조회 성공",
                    content = @Content(schema = @Schema(implementation = SearchHistoryListResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "items": [
                                        { "keyword": "러닝화", "searchedAt": "2026-06-20T10:00:00" },
                                        { "keyword": "선풍기", "searchedAt": "2026-06-19T18:00:00" }
                                      ]
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "로그인 필요",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"error\": \"로그인이 필요합니다.\"}")))
    })
    @GetMapping("/search/history")
    public SearchHistoryListResponse getHistories(@AuthenticationPrincipal Long userId) {

        return searchService.getHistories(userId);

    }

    @Operation(summary = "검색어 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "삭제 성공 (본문 없음)"),
            @ApiResponse(responseCode = "400", description = "keyword 누락",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"error\": \"keyword는 빈 값일 수 없습니다.\"}"))),
            @ApiResponse(responseCode = "401", description = "인증 필요",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"error\": \"인증이 필요합니다.\"}")))
    })
    @DeleteMapping("/search/history")
    public ResponseEntity<Void> deleteHistory(@AuthenticationPrincipal Long userId,
                                               @Parameter(description = "삭제할 검색어", required = true)
                                               @RequestParam @NotBlank(message = "keyword는 빈 값일 수 없습니다.") String keyword) {

        searchService.deleteHistory(userId, keyword);

        return ResponseEntity.noContent().build();

    }

}
