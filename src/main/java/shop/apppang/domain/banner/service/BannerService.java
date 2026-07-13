package shop.apppang.domain.banner.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shop.apppang.domain.banner.dto.BannerResponse;
import shop.apppang.domain.banner.entity.BannerEntity;
import shop.apppang.domain.banner.repository.BannerRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BannerService {

    private final BannerRepository bannerRepository;

    public List<BannerResponse> getBanners() {

        List<BannerEntity> banners = bannerRepository.findAll();

        return banners.stream()
                .map(banner -> new BannerResponse(
                        banner.getId(),
                        banner.getImageUrl(),
                        banner.getLinkUrl(),
                        banner.getDisplayOrder()
                ))
                .toList();
    }

}