package shop.apppang.domain.product.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shop.apppang.domain.order.entity.OrderEntity;
import shop.apppang.domain.order.entity.OrderItemEntity;
import shop.apppang.domain.order.repository.OrderItemRepository;
import shop.apppang.domain.order.repository.OrderRepository;
import shop.apppang.domain.product.dto.ProductDetailResponse;
import shop.apppang.domain.product.dto.ProductItemResponse;
import shop.apppang.domain.product.dto.ProductListResponse;
import shop.apppang.domain.product.entity.ProductEntity;
import shop.apppang.domain.product.entity.ProductImageEntity;
import shop.apppang.domain.product.repository.ProductImageRepository;
import shop.apppang.domain.product.repository.ProductRepository;
import shop.apppang.domain.review.dto.ReviewSummaryResponse;
import shop.apppang.domain.review.repository.ReviewRepository;
import shop.apppang.domain.wishlist.repository.WishlistRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    private final ProductImageRepository productImageRepository;

    private final ReviewRepository reviewRepository;

    private final WishlistRepository wishlistRepository;

    private final OrderRepository orderRepository;

    private final OrderItemRepository orderItemRepository;

    public ProductListResponse getProducts() {

        List<ProductEntity> products = productRepository.findAll();

        List<ProductItemResponse> items = products.stream()
                .map(product -> {

                    String imageUrl = productImageRepository
                            .findByProductAndIsMainTrue(product)
                            .map(ProductImageEntity::getImageUrl)
                            .orElse(null);

                    Double averageRating =
                            reviewRepository.findAverageRatingByProductId(product.getId());

                    if (averageRating == null) {
                        averageRating = 0.0;
                    }

                    Integer discountRate =
                            calculateDiscountRate(
                                    product.getPrice(),
                                    product.getOriginalPrice()
                            );

                    return new ProductItemResponse(
                            product.getId(),
                            product.getName(),
                            imageUrl,
                            product.getOptionInfo(),
                            product.getPrice(),
                            product.getOriginalPrice(),
                            discountRate,
                            product.getUnitPrice(),
                            averageRating,
                            (int) reviewRepository.countByProductId(product.getId()),
                            product.getShippingInfo(),
                            product.getRocketDelivery()
                    );

                }).toList();

        return new ProductListResponse(
                null,
                null,
                null,
                1,
                items.size(),
                items.size(),
                false,
                items
        );
    }

    public ProductDetailResponse getProduct(Long userId, Long productId) {

        ProductEntity product =
                productRepository.findById(productId)
                        .orElseThrow(() ->
                                new RuntimeException("상품을 찾을 수 없습니다.")
                        );

        Integer discountRate =
                calculateDiscountRate(
                        product.getPrice(),
                        product.getOriginalPrice()
                );

        List<String> images =
                productImageRepository.findByProduct(product)
                        .stream()
                        .map(ProductImageEntity::getImageUrl)
                        .toList();

        Double averageRating =
                reviewRepository.findAverageRatingByProductId(productId);

        if (averageRating == null) {
            averageRating = 0.0;
        }

        Long reviewCount =
                reviewRepository.countByProductId(productId);

        boolean isWished =
                wishlistRepository.existsByUser_IdAndProduct_Id(
                        userId,
                        productId
                );

        List<OrderEntity> orders =
                orderRepository.findByUser_IdOrderByCreatedAtDesc(userId);

        boolean canWriteReview = false;

        for (OrderEntity order : orders) {

            if (!"DELIVERED".equals(order.getStatus())) {
                continue;
            }

            List<OrderItemEntity> items =
                    orderItemRepository.findByOrder_Id(order.getId());

            if (items.stream().anyMatch(item ->
                    item.getProduct().getId().equals(productId))) {

                canWriteReview = true;
                break;
            }
        }

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
                null,
                product.getCategory().getId(),
                isWished,
                canWriteReview,
                new ReviewSummaryResponse(
                        averageRating,
                        reviewCount
                )
        );
    }

    private Integer calculateDiscountRate(
            Long price,
            Long originalPrice
    ) {

        if (originalPrice == null || originalPrice == 0) {
            return 0;
        }

        return (int) (((originalPrice - price) * 100) / originalPrice);

    }
}