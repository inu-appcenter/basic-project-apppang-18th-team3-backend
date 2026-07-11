package shop.apppang.domain.banner.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BannerResponse {

    private Long id;

    private String imageUrl;

    private String linkUrl;

    private Integer displayOrder;

}