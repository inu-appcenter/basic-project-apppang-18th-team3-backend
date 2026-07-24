package shop.apppang.domain.product.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import shop.apppang.domain.category.repository.CategoryRepository;
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
import shop.apppang.domain.search.repository.SearchHistoryRepository;
import shop.apppang.domain.wishlist.repository.WishlistRepository;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
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
    private final CategoryRepository categoryRepository;

    // 1. 상품 목록 조회 (명세서 쿼리 파라미터 및 필터 완벽 지원)
    public ProductListResponse getProducts(
            Long categoryId,
            String keyword,
            Long minPrice,
            Long maxPrice,
            Boolean rocketDelivery,
            String sort,
            int page,
            int size,
            Pageable pageable
    ) {
        // DB 필터링 수행 (sort 파라미터 제거하여 6개 인자로 호출)
        Page<ProductEntity> productPage = productRepository.findProductsWithFilters(
                categoryId, keyword, minPrice, maxPrice, rocketDelivery, pageable
        );

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

        // category_name 조회
        String categoryName = null;
        if (categoryId != null) {
            categoryName = categoryRepository.findById(categoryId)
                    .map(category -> category.getName())
                    .orElse(null);
        }

        return new ProductListResponse(
                categoryId,
                categoryName,
                keyword,
                page, // 프론트 요구 1-based page 번호
                size,
                (int) productPage.getTotalElements(),
                productPage.hasNext(),
                items
        );
    }

    // 2. 상품 상세 조회 (404 예외 처리 적용)
    public ProductDetailResponse getProduct(Long userId, Long productId) {
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("상품을 찾을 수 없습니다. id=" + productId));

        Integer discountRate = calculateDiscountRate(product.getPrice(), product.getOriginalPrice());

        List<ProductImageEntity> productImages = productImageRepository.findByProduct(product);
        List<String> images = productImages.stream().map(ProductImageEntity::getImageUrl).toList();
        List<String> detailImages = product.getDetailContent() != null ? List.of(product.getDetailContent()) : List.of();

        boolean isWished = (userId != null) && wishlistRepository.existsByUser_IdAndProduct_Id(userId, productId);

        boolean canWriteReview = false;
        if (userId != null) {
            canWriteReview = orderItemRepository.findAll().stream()
                    .filter(item -> item != null && item.getOrder() != null && item.getProduct() != null)
                    .filter(item -> item.getOrder().getUser() != null)
                    .anyMatch(item -> userId.equals(item.getOrder().getUser().getId())
                            && productId.equals(item.getProduct().getId()));
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

    // 대표 이미지 안전 매핑 (없는 경우 대체 이미지 적용)
    private Map<Long, String> getMainImageMap(List<ProductEntity> products) {
        if (products == null || products.isEmpty()) {
            return Map.of();
        }
        List<Long> productIds = products.stream().map(ProductEntity::getId).toList();
        List<ProductImageEntity> mainImages = productImageRepository.findByProductIdInAndIsMainTrue(productIds);

        Map<Long, String> mainImageMap = mainImages.stream()
                .collect(Collectors.toMap(
                        img -> img.getProduct().getId(),
                        ProductImageEntity::getImageUrl,
                        (existing, replacement) -> existing
                ));

        for (ProductEntity product : products) {
            if (!mainImageMap.containsKey(product.getId())) {
                List<ProductImageEntity> allImages = productImageRepository.findByProduct(product);
                if (!allImages.isEmpty()) {
                    mainImageMap.put(product.getId(), allImages.get(0).getImageUrl());
                } else {
                    mainImageMap.put(product.getId(), null);
                }
            }
        }

        return mainImageMap;
    }

    private Integer calculateDiscountRate(Long price, Long originalPrice) {
        if (originalPrice == null || originalPrice == 0) {
            return 0;
        }
        return (int) (((originalPrice - price) * 100) / originalPrice);
    }
}