package shop.apppang.domain.banner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.apppang.domain.banner.entity.BannerEntity;

import java.util.List;

public interface BannerRepository extends JpaRepository<BannerEntity, Long> {

    // isActive가 true인 배너만 displayOrder 오름차순으로 조회
    List<BannerEntity> findAllByIsActiveTrueOrderByDisplayOrderAsc();

}