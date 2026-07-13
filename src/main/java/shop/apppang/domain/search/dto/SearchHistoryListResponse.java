package shop.apppang.domain.search.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class SearchHistoryListResponse {

    private List<Item> items;

    @Getter
    @AllArgsConstructor
    public static class Item {

        private String keyword;

        private LocalDateTime searchedAt;

    }

}
