package shop.apppang.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.apppang.domain.auth.exception.MemberNotFoundException;
import shop.apppang.domain.product.entity.ProductEntity;
import shop.apppang.domain.product.entity.ProductImageEntity;
import shop.apppang.domain.product.repository.ProductImageRepository;
import shop.apppang.domain.product.repository.ProductRepository;
import shop.apppang.domain.search.entity.SearchHistory;
import shop.apppang.domain.search.repository.SearchHistoryRepository;
import shop.apppang.domain.user.dto.RecentProductResponse;
import shop.apppang.domain.user.entity.User;
import shop.apppang.domain.user.repository.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final SearchHistoryRepository searchHistoryRepository;

    private final ProductRepository productRepository;

    private final ProductImageRepository productImageRepository;

    @Transactional(readOnly = true)
    public RecentProductResponse getRecentProducts(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new MemberNotFoundException("사용자를 찾을 수 없습니다."));

        return searchHistoryRepository.findTopByUserOrderBySearchedAtDesc(user)
                .map(SearchHistory::getKeyword)
                .map(this::findRecentProductsByKeyword)
                .orElseGet(() -> new RecentProductResponse(List.of()));
    }

    private RecentProductResponse findRecentProductsByKeyword(String keyword) {

        List<ProductEntity> products = productRepository
                .findTop20ByNameContainingIgnoreCaseAndIsActiveTrueOrderByIdAsc(keyword);

        List<Long> productIds = products.stream()
                .map(ProductEntity::getId)
                .toList();

        Map<Long, String> mainImageUrlByProductId = productImageRepository
                .findByProductIdInAndIsMainTrue(productIds).stream()
                .collect(Collectors.toMap(
                        image -> image.getProduct().getId(),
                        ProductImageEntity::getImageUrl,
                        (existing, replacement) -> existing
                ));

        List<RecentProductResponse.Item> items = products.stream()
                .map(product -> new RecentProductResponse.Item(
                        product.getId(),
                        product.getName(),
                        mainImageUrlByProductId.get(product.getId()),
                        product.getPrice()
                ))
                .toList();

        return new RecentProductResponse(items);
    }

}
