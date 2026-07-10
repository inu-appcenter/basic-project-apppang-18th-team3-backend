package shop.apppang.domain.product.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shop.apppang.domain.product.dto.ProductItemResponse;
import shop.apppang.domain.product.dto.ProductListResponse;
import shop.apppang.domain.product.entity.ProductEntity;
import shop.apppang.domain.product.repository.ProductRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    // 상품 목록 조회
    public ProductListResponse getProducts() {

        // 모든 상품 조회
        List<ProductEntity> products = productRepository.findAll();

        // Entity → DTO 변환
        List<ProductItemResponse> items = products.stream()
                .map(product -> {

                    Integer discountRate = calculateDiscountRate(
                            product.getPrice(),
                            product.getOriginalPrice()
                    );

                    return new ProductItemResponse(
                            product.getId(),
                            product.getName(),
                            null,                           // 대표 이미지(추후 구현)
                            product.getOptionInfo(),
                            product.getPrice(),
                            product.getOriginalPrice(),
                            discountRate,
                            product.getUnitPrice(),
                            0.0,                            // 평균 평점(추후 구현)
                            0,                              // 리뷰 개수(추후 구현)
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

    // 할인율 계산
    private Integer calculateDiscountRate(Long price, Long originalPrice) {

        if (originalPrice == null || originalPrice == 0) {
            return 0;
        }

        return (int) (((originalPrice - price) * 100) / originalPrice);

    }

}