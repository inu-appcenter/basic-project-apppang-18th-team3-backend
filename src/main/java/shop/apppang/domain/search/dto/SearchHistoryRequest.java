package shop.apppang.domain.search.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SearchHistoryRequest {

    @Schema(description = "저장할 검색어 (최대 50자)", example = "러닝화")
    @NotBlank(message = "검색어를 입력해주세요.")
    @Size(max = 50, message = "검색어는 50자 이하로 입력해주세요.")
    private String keyword;

}
