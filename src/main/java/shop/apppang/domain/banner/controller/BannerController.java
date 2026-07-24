package shop.apppang.domain.banner.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.apppang.domain.banner.dto.BannerResponse;
import shop.apppang.domain.banner.service.BannerService;

import java.util.List;

@Tag(name = "배너 API", description = "메인 홈 배너 관련 API")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BannerController {

    private final BannerService bannerService;

    @Operation(summary = "메인 홈 배너 목록 조회")
    @GetMapping("/banners")
    public ResponseEntity<List<BannerResponse>> getBanners() {
        return ResponseEntity.ok(bannerService.getBanners());
    }

}