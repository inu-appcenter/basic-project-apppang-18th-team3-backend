package shop.apppang.domain.search.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SearchHistoryRequest {

    @NotBlank(message = "검색어를 입력해주세요.")
    @Size(max = 50, message = "검색어는 50자 이하로 입력해주세요.")
    private String keyword;

}
