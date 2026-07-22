package shop.apppang.domain.banner.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.apppang.domain.banner.dto.BannerResponse;
import shop.apppang.domain.banner.service.BannerService;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BannerController {

    private final BannerService bannerService;

    @Operation(summary = "메인 배너 목록")
    @GetMapping("/banners")
    public List<BannerResponse> getBanners() {

        return bannerService.getBanners();

    }

}