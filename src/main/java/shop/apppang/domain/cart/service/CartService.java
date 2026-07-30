package shop.apppang.domain.cart.service;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import shop.apppang.domain.cart.dto.CartItemResponse;
import shop.apppang.domain.cart.dto.CartResponse;
import shop.apppang.domain.cart.entity.CartItemEntity;
import shop.apppang.domain.cart.repository.CartRepository;
import shop.apppang.domain.product.entity.ProductEntity;
import shop.apppang.domain.product.entity.ProductImageEntity;
import shop.apppang.domain.product.repository.ProductImageRepository;
import shop.apppang.domain.user.entity.User;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductImageRepository productImageRepository;
    private final EntityManager em;

    // 조회
    @Transactional(readOnly = true)
    public CartResponse getCart(Long userId) {
        List<CartItemEntity> cartItems = cartRepository.findByUser_Id(userId);
        List<Long> productIds = cartItems.stream()
                .map(c -> c.getProduct().getId())
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
        return CartResponse.from(cartItems, mainImageMap);
    }

    // 담기 (이미 있으면 수량 합산)
    @Transactional
    public CartItemResponse addToCart(Long userId, Long productId, Integer quantity) {
        int qty = (quantity == null || quantity < 1) ? 1 : quantity;

        Optional<CartItemEntity> existing = cartRepository.findByUser_IdAndProduct_Id(userId, productId);
        if (existing.isPresent()) {
            CartItemEntity item = existing.get();
            item.addQuantity(qty);                 // 합산
            return CartItemResponse.from(item, null);
        }
        User user = em.getReference(User.class, userId);
        ProductEntity product = em.getReference(ProductEntity.class, productId);
        CartItemEntity item = CartItemEntity.builder()
                .user(user).product(product).quantity(qty).build();
        return CartItemResponse.from(cartRepository.save(item), null);
    }

    // 수량 변경
    @Transactional
    public CartItemResponse updateQuantity(Long userId, Long cartItemId, Integer quantity) {
        if (quantity == null || quantity < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수량은 1개 이상이어야 합니다");
        }
        CartItemEntity item = findMyCartItem(userId, cartItemId);
        item.changeQuantity(quantity);
        return CartItemResponse.from(item, null);
    }

    // 삭제
    @Transactional
    public void deleteCartItem(Long userId, Long cartItemId) {
        CartItemEntity item = findMyCartItem(userId, cartItemId);
        cartRepository.delete(item);
    }

    // 주문 완료 시 호출: 주문한 상품과 일치하는 장바구니 항목만 삭제
    // 장바구니를 거치지 않은 주문이면 삭제 대상이 없어 아무 일도 일어나지 않는다
    @Transactional
    public void removeOrderedItems(Long userId, List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) return;
        cartRepository.deleteByUser_IdAndProduct_IdIn(userId, productIds);
    }

    private CartItemEntity findMyCartItem(Long userId, Long cartItemId) {
        CartItemEntity item = cartRepository.findById(cartItemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "장바구니 상품을 찾을 수 없습니다"));
        if (!item.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인의 장바구니만 접근할 수 있습니다");
        }
        return item;
    }
}