package shop.apppang.domain.banner.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.apppang.domain.banner.dto.BannerResponse;
import shop.apppang.domain.banner.repository.BannerRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BannerService {

    private final BannerRepository bannerRepository;

    public List<BannerResponse> getBanners() {
        return bannerRepository.findAllByIsActiveTrueOrderByDisplayOrderAsc()
                .stream()
                .map(BannerResponse::from)
                .toList();
    }

}