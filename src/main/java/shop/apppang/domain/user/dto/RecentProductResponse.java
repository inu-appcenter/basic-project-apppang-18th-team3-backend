package shop.apppang.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class RecentProductResponse {

    private List<Item> items;

    @Getter
    @AllArgsConstructor
    public static class Item {

        private Long productId;

        private String name;

        private String imageUrl;

        private Long price;

    }

}
