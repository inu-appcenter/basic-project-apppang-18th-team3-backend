package shop.apppang.domain.wishlist.service;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import shop.apppang.domain.product.entity.ProductEntity;
import shop.apppang.domain.product.entity.ProductImageEntity;
import shop.apppang.domain.product.repository.ProductImageRepository;
import shop.apppang.domain.user.entity.User;
import shop.apppang.domain.wishlist.dto.WishlistResponse;
import shop.apppang.domain.wishlist.entity.WishlistEntity;
import shop.apppang.domain.wishlist.repository.WishlistRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductImageRepository productImageRepository;
    private final EntityManager em;

    @Transactional(readOnly = true)
    public List<WishlistResponse> getWishlist(Long userId) {
        List<WishlistEntity> wishlists = wishlistRepository.findByUser_Id(userId);
        List<Long> productIds = wishlists.stream()
                .map(w -> w.getProduct().getId())
                .distinct()
                .toList();
        Map<Long, String> mainImageMap = productIds.isEmpty()
                ? Map.of()
                : productImageRepository.findByProductIdInAndIsMainTrue(productIds).stream()
                        .collect(Collectors.toMap(
                                img -> img.getProduct().getId(),
                                ProductImageEntity::getImageUrl,
                                (existing, replacement) -> existing
                        ));
        return wishlists.stream()
                .map(w -> WishlistResponse.from(w, mainImageMap.get(w.getProduct().getId())))
                .toList();
    }

    @Transactional
    public WishlistResponse addWishlist(Long userId, Long productId) {
        if (wishlistRepository.existsByUser_IdAndProduct_Id(userId, productId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 찜한 상품입니다");
        }
        User user = em.getReference(User.class, userId);
        ProductEntity product = em.getReference(ProductEntity.class, productId);

        WishlistEntity wishlist = WishlistEntity.builder()
                .user(user)
                .product(product)
                .build();
        String imageUrl = productImageRepository.findByProductIdInAndIsMainTrue(List.of(productId)).stream()
                .findFirst()
                .map(ProductImageEntity::getImageUrl)
                .orElse(null);
        return WishlistResponse.from(wishlistRepository.save(wishlist), imageUrl);
    }

    @Transactional
    public void removeWishlist(Long userId, Long productId) {
        WishlistEntity wishlist = wishlistRepository.findByUser_IdAndProduct_Id(userId, productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "찜하지 않은 상품입니다"));
        wishlistRepository.delete(wishlist);
    }
}