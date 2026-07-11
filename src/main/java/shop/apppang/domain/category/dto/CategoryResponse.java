package shop.apppang.domain.category.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CategoryResponse {

    private List<Item> items;

    @Getter
    @AllArgsConstructor
    public static class Item {

        private Long categoryId;

        private String name;

        private String iconUrl;

    }

}