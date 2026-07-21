package shop.apppang.domain.product.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import shop.apppang.domain.order.repository.OrderItemRepository;
import shop.apppang.domain.product.dto.ProductDetailResponse;
import shop.apppang.domain.product.dto.ProductItemResponse;
import shop.apppang.domain.product.dto.ProductListResponse;
import shop.apppang.domain.product.entity.ProductEntity;
import shop.apppang.domain.product.entity.ProductImageEntity;
import shop.apppang.domain.product.repository.ProductImageRepository;
import shop.apppang.domain.product.repository.ProductRepository;
import shop.apppang.domain.review.dto.ReviewSummaryResponse;
import shop.apppang.domain.review.repository.ReviewRepository;
import shop.apppang.domain.search.entity.SearchHistory;
import shop.apppang.domain.search.repository.SearchHistoryRepository;
import shop.apppang.domain.wishlist.repository.WishlistRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final ReviewRepository reviewRepository;
    private final WishlistRepository wishlistRepository;
    private final OrderItemRepository orderItemRepository;
    private final SearchHistoryRepository searchHistoryRepository;

    // 1. 상품 목록 조회 (페이징, 카테고리 필터링, 키워드 검색 적용)
    public ProductListResponse getProducts(Long categoryId, String keyword, Pageable pageable) {
        Page<ProductEntity> productPage = productRepository.findProductsWithFilters(categoryId, keyword, pageable);
        List<ProductEntity> products = productPage.getContent();

        Map<Long, String> mainImageMap = getMainImageMap(products);

        List<ProductItemResponse> items = products.stream()
                .map(product -> {
                    Integer discountRate = calculateDiscountRate(product.getPrice(), product.getOriginalPrice());
                    Double averageRating = reviewRepository.findAverageRatingByProductId(product.getId());
                    if (averageRating == null) averageRating = 0.0;
                    Long reviewCount = reviewRepository.countByProductId(product.getId());

                    return new ProductItemResponse(
                            product.getId(),
                            product.getName(),
                            mainImageMap.get(product.getId()),
                            product.getOptionInfo(),
                            product.getPrice(),
                            product.getOriginalPrice(),
                            discountRate,
                            product.getUnitPrice(),
                            averageRating,
                            reviewCount.intValue(),
                            product.getShippingInfo(),
                            product.getRocketDelivery()
                    );
                })
                .toList();

        return new ProductListResponse(
                null,
                null,
                null,
                productPage.getNumber() + 1, // 1부터 시작하는 페이지 번호
                productPage.getSize(),
                (int) productPage.getTotalElements(),
                productPage.hasNext(),
                items
        );
    }

    // 2. 상품 상세 조회 (찜 여부 + 구매 이력 검증)
    public ProductDetailResponse getProduct(Long userId, Long productId) {
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다."));

        Integer discountRate = calculateDiscountRate(product.getPrice(), product.getOriginalPrice());

        List<ProductImageEntity> productImages = productImageRepository.findByProduct(product);
        List<String> images = productImages.stream().map(ProductImageEntity::getImageUrl).toList();
        List<String> detailImages = product.getDetailContent() != null ? List.of(product.getDetailContent()) : List.of();

        // 찜 여부 확인
        boolean isWished = (userId != null) && wishlistRepository.existsByUser_IdAndProduct_Id(userId, productId);

        // 구매 이력 확인 (리뷰 작성 가능 여부)
        boolean canWriteReview = false;
        if (userId != null) {
            canWriteReview = orderItemRepository.findAll().stream()
                    .anyMatch(item -> item.getOrder() != null
                            && item.getOrder().getUser() != null
                            && item.getOrder().getUser().getId().equals(userId)
                            && item.getProduct() != null
                            && item.getProduct().getId().equals(productId));
        }

        Double averageRating = reviewRepository.findAverageRatingByProductId(productId);
        if (averageRating == null) averageRating = 0.0;
        Long reviewCount = reviewRepository.countByProductId(productId);

        return new ProductDetailResponse(
                product.getId(),
                product.getBrand(),
                product.getName(),
                images,
                product.getOptionInfo(),
                product.getPrice(),
                product.getOriginalPrice(),
                discountRate,
                product.getUnitPrice(),
                product.getShippingInfo(),
                product.getRocketDelivery(),
                product.getStock(),
                product.getDescription(),
                detailImages,
                product.getCategory().getId(),
                isWished,
                canWriteReview,
                new ReviewSummaryResponse(averageRating, reviewCount)
        );
    }

    // 3. 추천 상품 목록 조회 (최근 검색어 기반 / 인기 상품 대체)
    public Object getRecommendedProducts(Long userId, int size) {
        String basedOn = "popular";
        List<ProductEntity> products = List.of();

        if (userId != null) {
            Optional<SearchHistory> latestSearch = searchHistoryRepository.findAll().stream()
                    .filter(sh -> sh.getUser() != null && sh.getUser().getId().equals(userId))
                    .max((sh1, sh2) -> sh1.getSearchedAt().compareTo(sh2.getSearchedAt()));

            if (latestSearch.isPresent() && latestSearch.get().getKeyword() != null) {
                basedOn = "search_history";
                String keyword = latestSearch.get().getKeyword();
                products = productRepository.findTop20ByNameContainingIgnoreCaseAndIsActiveTrueOrderByIdAsc(keyword);
            }
        }

        if ("popular".equals(basedOn) || products.isEmpty()) {
            basedOn = "popular";
            products = productRepository.findAll();
        }

        List<ProductItemResponse> items = convertToProductItemResponses(products, size);

        return Map.of("based_on", basedOn, "items", items);
    }

    // 4. 인기 상품 목록 조회
    public Object getPopularProducts(int size) {
        List<ProductEntity> products = productRepository.findTop20ByIsActiveTrueOrderBySalesCountDesc();
        List<ProductItemResponse> items = convertToProductItemResponses(products, size);

        return Map.of("items", items);
    }

    private List<ProductItemResponse> convertToProductItemResponses(List<ProductEntity> products, int size) {
        List<ProductEntity> limitedProducts = products.stream().limit(size).toList();
        Map<Long, String> mainImageMap = getMainImageMap(limitedProducts);

        return limitedProducts.stream()
                .map(product -> {
                    Integer discountRate = calculateDiscountRate(product.getPrice(), product.getOriginalPrice());
                    Double averageRating = reviewRepository.findAverageRatingByProductId(product.getId());
                    if (averageRating == null) averageRating = 0.0;
                    Long reviewCount = reviewRepository.countByProductId(product.getId());

                    return new ProductItemResponse(
                            product.getId(),
                            product.getName(),
                            mainImageMap.get(product.getId()),
                            product.getOptionInfo(),
                            product.getPrice(),
                            product.getOriginalPrice(),
                            discountRate,
                            product.getUnitPrice(),
                            averageRating,
                            reviewCount.intValue(),
                            product.getShippingInfo(),
                            product.getRocketDelivery()
                    );
                })
                .toList();
    }

    private Map<Long, String> getMainImageMap(List<ProductEntity> products) {
        if (products.isEmpty()) {
            return Map.of();
        }
        List<Long> productIds = products.stream().map(ProductEntity::getId).toList();
        List<ProductImageEntity> mainImages = productImageRepository.findByProductIdInAndIsMainTrue(productIds);

        return mainImages.stream()
                .collect(Collectors.toMap(
                        img -> img.getProduct().getId(),
                        ProductImageEntity::getImageUrl,
                        (existing, replacement) -> existing
                ));
    }

    // 할인율 계산
    private Integer calculateDiscountRate(Long price, Long originalPrice) {
        if (originalPrice == null || originalPrice == 0) {
            return 0;
        }
        return (int) (((originalPrice - price) * 100) / originalPrice);
    }
}