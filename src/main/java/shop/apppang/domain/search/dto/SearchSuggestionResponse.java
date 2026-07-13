package shop.apppang.domain.search.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class SearchSuggestionResponse {

    private List<String> suggestions;

}
