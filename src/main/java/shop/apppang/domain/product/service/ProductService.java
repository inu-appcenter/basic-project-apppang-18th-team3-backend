package shop.apppang.domain.product.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shop.apppang.domain.product.dto.ProductDetailResponse;
import shop.apppang.domain.product.dto.ProductItemResponse;
import shop.apppang.domain.product.dto.ProductListResponse;
import shop.apppang.domain.product.entity.ProductEntity;
import shop.apppang.domain.product.repository.ProductImageRepository;
import shop.apppang.domain.product.repository.ProductRepository;
import shop.apppang.domain.review.dto.ReviewSummaryResponse;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    private final ProductImageRepository productImageRepository;

    // 상품 목록 조회
    public ProductListResponse getProducts() {

        List<ProductEntity> products = productRepository.findAll();

        List<ProductItemResponse> items = products.stream()
                .map(product -> {
                    Integer discountRate = calculateDiscountRate(
                            product.getPrice(),
                            product.getOriginalPrice()
                    );
                    return new ProductItemResponse(
                            product.getId(),
                            product.getName(),
                            null,                       // 대표 이미지 (추후 구현)
                            product.getOptionInfo(),
                            product.getPrice(),
                            product.getOriginalPrice(),
                            discountRate,
                            product.getUnitPrice(),
                            0.0,                        // 평균 평점 (추후 구현)
                            0,                          // 리뷰 개수 (추후 구현)
                            product.getShippingInfo(),
                            product.getRocketDelivery()
                    );
                })
                .toList();

        return new ProductListResponse(
                null,                   // categoryId
                null,                   // categoryName
                null,                   // keyword
                1,                      // page
                items.size(),           // size
                items.size(),           // total
                false,                  // hasNext
                items
        );
    }

    // 상품 상세 조회
    public ProductDetailResponse getProduct(Long productId) {

        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));

        Integer discountRate = calculateDiscountRate(product.getPrice(), product.getOriginalPrice());

        return new ProductDetailResponse(
                product.getId(),
                product.getBrand(),
                product.getName(),
                null,                               // images (추후 구현)
                product.getOptionInfo(),
                product.getPrice(),
                product.getOriginalPrice(),
                discountRate,
                product.getUnitPrice(),
                product.getShippingInfo(),
                product.getRocketDelivery(),
                product.getStock(),
                product.getDescription(),
                null,                               // detailImages (추후 구현)
                product.getCategory().getId(),
                false,                              // isWished (추후 구현)
                false,                              // canWriteReview (추후 구현)
                new ReviewSummaryResponse()         // reviewSummary (추후 구현)
        );
    }

    // 할인율 계산
    private Integer calculateDiscountRate(Long price, Long originalPrice) {
        if (originalPrice == null || originalPrice == 0) {
            return 0;
        }
        return (int) (((originalPrice - price) * 100) / originalPrice);
    }
}