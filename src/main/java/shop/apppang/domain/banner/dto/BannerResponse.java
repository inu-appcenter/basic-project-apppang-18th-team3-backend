package shop.apppang.domain.banner.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import shop.apppang.domain.banner.entity.BannerEntity;

@Getter
@AllArgsConstructor
public class BannerResponse {

    private Long id;

    private String imageUrl;

    private String linkUrl;

    private Integer displayOrder;

    // S3 버킷 기본 주소
    private static final String BASE_URL = "https://apppang-images.s3.ap-northeast-2.amazonaws.com";

    public static BannerResponse from(BannerEntity banner) {
        String fullImageUrl = banner.getImageUrl();

        // imageUrl이 null이 아니고 상대 경로(/)로 시작하면 S3 기본 주소를 결합
        if (fullImageUrl != null && fullImageUrl.startsWith("/")) {
            fullImageUrl = BASE_URL + fullImageUrl;
        }

        return new BannerResponse(
                banner.getId(),
                fullImageUrl,
                banner.getLinkUrl(),
                banner.getDisplayOrder()
        );
    }
}