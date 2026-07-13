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
import shop.apppang.domain.user.entity.User;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final EntityManager em;

    // 조회
    @Transactional(readOnly = true)
    public CartResponse getCart(Long userId) {
        return CartResponse.from(cartRepository.findByUser_Id(userId));
    }

    // 담기 (이미 있으면 수량 합산)
    @Transactional
    public CartItemResponse addToCart(Long userId, Long productId, Integer quantity) {
        int qty = (quantity == null || quantity < 1) ? 1 : quantity;

        Optional<CartItemEntity> existing = cartRepository.findByUser_IdAndProduct_Id(userId, productId);
        if (existing.isPresent()) {
            CartItemEntity item = existing.get();
            item.addQuantity(qty);                 // 합산
            return CartItemResponse.from(item);
        }
        User user = em.getReference(User.class, userId);
        ProductEntity product = em.getReference(ProductEntity.class, productId);
        CartItemEntity item = CartItemEntity.builder()
                .user(user).product(product).quantity(qty).build();
        return CartItemResponse.from(cartRepository.save(item));
    }

    // 수량 변경
    @Transactional
    public CartItemResponse updateQuantity(Long userId, Long cartItemId, Integer quantity) {
        if (quantity == null || quantity < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수량은 1개 이상이어야 합니다");
        }
        CartItemEntity item = findMyCartItem(userId, cartItemId);
        item.changeQuantity(quantity);
        return CartItemResponse.from(item);
    }

    // 삭제
    @Transactional
    public void deleteCartItem(Long userId, Long cartItemId) {
        CartItemEntity item = findMyCartItem(userId, cartItemId);
        cartRepository.delete(item);
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