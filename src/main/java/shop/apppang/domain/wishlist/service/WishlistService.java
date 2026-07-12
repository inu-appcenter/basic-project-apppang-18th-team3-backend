package shop.apppang.domain.wishlist.service;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import shop.apppang.domain.product.entity.ProductEntity;
import shop.apppang.domain.user.entity.UserEntity;
import shop.apppang.domain.wishlist.dto.WishlistResponse;
import shop.apppang.domain.wishlist.entity.WishlistEntity;
import shop.apppang.domain.wishlist.repository.WishlistRepository;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final EntityManager em;

    @Transactional(readOnly = true)
    public List<WishlistResponse> getWishlist(Long userId) {
        return wishlistRepository.findByUser_Id(userId).stream()
                .map(WishlistResponse::from)
                .toList();
    }

    @Transactional
    public WishlistResponse addWishlist(Long userId, Long productId) {
        if (wishlistRepository.existsByUser_IdAndProduct_Id(userId, productId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 찜한 상품입니다");
        }
        UserEntity user = em.getReference(UserEntity.class, userId);
        ProductEntity product = em.getReference(ProductEntity.class, productId);

        WishlistEntity wishlist = WishlistEntity.builder()
                .user(user)
                .product(product)
                .build();
        return WishlistResponse.from(wishlistRepository.save(wishlist));
    }

    @Transactional
    public void removeWishlist(Long userId, Long productId) {
        WishlistEntity wishlist = wishlistRepository.findByUser_IdAndProduct_Id(userId, productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "찜하지 않은 상품입니다"));
        wishlistRepository.delete(wishlist);
    }
}