package shop.apppang.domain.banner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.apppang.domain.banner.entity.BannerEntity;

public interface BannerRepository extends JpaRepository<BannerEntity, Long> {

}