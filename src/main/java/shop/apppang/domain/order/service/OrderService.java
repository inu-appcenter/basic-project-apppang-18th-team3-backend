package shop.apppang.domain.order.service;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import shop.apppang.domain.address.entity.AddressEntity;
import shop.apppang.domain.cart.service.CartService;
import shop.apppang.domain.order.dto.*;
import shop.apppang.domain.order.entity.OrderEntity;
import shop.apppang.domain.order.entity.OrderItemEntity;
import shop.apppang.domain.order.repository.OrderItemRepository;
import shop.apppang.domain.order.repository.OrderRepository;
import shop.apppang.domain.product.entity.ProductEntity;
import shop.apppang.domain.product.entity.ProductImageEntity;
import shop.apppang.domain.product.repository.ProductImageRepository;
import shop.apppang.domain.user.entity.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductImageRepository productImageRepository;
    private final CartService cartService;
    private final EntityManager em;

    // 주문에 담을 상품+수량 임시 보관용
    private record Line(ProductEntity product, int quantity) {}

    // ① 주문 생성 (결제 + 재고 차감, 전부 하나의 트랜잭션)
    @Transactional
    public OrderCreateResponse createOrder(Long userId, OrderCreateRequest req) {
        if (req.addressId() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "배송지를 선택해주세요");
        if (req.paymentMethod() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "결제 수단을 선택해주세요");
        if (req.items() == null || req.items().isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "주문 상품이 없습니다");

        // 배송지 조회 + 본인 확인
        AddressEntity address = em.find(AddressEntity.class, req.addressId());
        if (address == null || !address.getUserId().equals(userId)) //addressEntity로 인해서 관계형이 아님
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "배송지를 찾을 수 없습니다");

        // 상품 로드 + 재고 확인 + 금액 계산
        long productAmount = 0;
        List<Line> lines = new ArrayList<>();
        for (OrderItemRequest it : req.items()) {
            ProductEntity p = em.find(ProductEntity.class, it.productId());
            if (p == null)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다: " + it.productId());
            int qty = (it.quantity() == null || it.quantity() < 1) ? 1 : it.quantity();
            if (p.getStock() != null && p.getStock() < qty)
                throw new ResponseStatusException(HttpStatus.CONFLICT, "재고가 부족합니다: " + p.getName());
            productAmount += p.getPrice() * qty;
            lines.add(new Line(p, qty));
        }
        long discountAmount = 0;
        long shippingFee = 0;
        long totalPrice = productAmount - discountAmount + shippingFee;

        // 앱팡 머니 확인 + 차감
        User user = em.find(User.class, userId);
        if (user == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다");
        if (user.getAppMoney() < totalPrice)
            throw new ResponseStatusException(HttpStatus.PAYMENT_REQUIRED, "앱팡 머니 잔액이 부족합니다");
        user.useMoney(totalPrice);

        // 주문 생성 (배송지·결제 스냅샷)
        OrderEntity order = OrderEntity.builder()
                .user(user)
                .shippingRecipientName(address.getRecipientName())
                .shippingPhone(address.getPhone()) // AddressEntity
                .shippingZipcode(address.getZipcode())
                .shippingAddress(address.getAddress())
                .shippingDetailAddress(address.getDetailAddress())
                .deliveryRequest(req.deliveryRequest())
                .productAmount(productAmount)
                .discountAmount(discountAmount)
                .shippingFee(shippingFee)
                .paymentMethod(req.paymentMethod())
                .totalPrice(totalPrice)
                .status("주문접수")
                .build();
        orderRepository.save(order);

        // 주문상세 생성 + 재고 차감
        for (Line line : lines) {
            line.product().decreaseStock(line.quantity());
            OrderItemEntity oi = OrderItemEntity.builder()
                    .order(order)
                    .product(line.product())
                    .quantity(line.quantity())
                    .price(line.product().getPrice())   // 주문 당시 가격 스냅샷
                    .status("주문접수")
                    .build();
            orderItemRepository.save(oi);
        }

        // 주문한 상품과 일치하는 장바구니 항목 정리 (같은 트랜잭션 → 주문 롤백 시 함께 롤백)
        List<Long> orderedProductIds = lines.stream()
                .map(line -> line.product().getId())
                .toList();
        cartService.removeOrderedItems(userId, orderedProductIds);

        return new OrderCreateResponse(order.getId(), totalPrice, order.getStatus(), user.getAppMoney());
    }

    // ② 주문 목록 (페이징)
    @Transactional(readOnly = true)
    public OrderListResponse getOrders(Long userId, Pageable pageable) {
        // 이 유저의 주문을 한 페이지(예: 10개)만 DB에서 가져온다. 전체를 메모리에 올리지 않는다.
        Page<OrderEntity> orderPage = orderRepository.findByUser_Id(userId, pageable);
        List<OrderEntity> orders = orderPage.getContent();

        // N+1 방지: 이 페이지 주문들의 ID를 모아 주문상세를 IN 절로 "한 번에" 조회한 뒤
        // 주문 ID 기준으로 그룹핑해 둔다. (주문마다 개별 쿼리를 날리지 않기 위함)
        List<Long> orderIds = orders.stream().map(OrderEntity::getId).toList();
        List<OrderItemEntity> allItems = orderIds.isEmpty()
                ? List.of()
                : orderItemRepository.findByOrder_IdIn(orderIds);
        Map<Long, List<OrderItemEntity>> itemsByOrderId = allItems.stream()
                .collect(Collectors.groupingBy(oi -> oi.getOrder().getId()));

        // 대표 이미지도 상품 ID를 모아 한 번에 조회 (주문상세마다 개별 쿼리를 날리지 않기 위함)
        Map<Long, String> mainImageMap = getMainImageMap(allItems);

        // 미리 그룹핑해 둔 주문상세를 꺼내 쓰므로 여기서는 추가 쿼리가 발생하지 않는다.
        List<OrderSummaryResponse> items = orders.stream()
                .map(o -> OrderSummaryResponse.from(o, itemsByOrderId.getOrDefault(o.getId(), List.of()), mainImageMap))
                .toList();

        return new OrderListResponse(
                pageable.getPageNumber() + 1,          // JPA의 0-based → 프론트 1-based로 환산
                pageable.getPageSize(),
                (int) orderPage.getTotalElements(),    // 전체 주문 수
                orderPage.hasNext(),                   // 다음 페이지 존재 여부
                items
        );
    }

    // ③ 주문 상세
    @Transactional(readOnly = true)
    public OrderDetailResponse getOrderDetail(Long userId, Long orderId) {
        OrderEntity order = findMyOrder(userId, orderId);
        List<OrderItemEntity> items = orderItemRepository.findByOrder_Id(orderId);
        return OrderDetailResponse.from(order, items, getMainImageMap(items));
    }

    // 주문상세 목록의 상품 ID를 모아 대표 이미지를 한 번에 조회 (없으면 null)
    private Map<Long, String> getMainImageMap(List<OrderItemEntity> orderItems) {
        List<Long> productIds = orderItems.stream()
                .map(oi -> oi.getProduct().getId())
                .distinct()
                .toList();
        if (productIds.isEmpty()) {
            return Map.of();
        }
        return productImageRepository.findByProductIdInAndIsMainTrue(productIds).stream()
                .collect(Collectors.toMap(
                        img -> img.getProduct().getId(),
                        ProductImageEntity::getImageUrl,
                        (existing, replacement) -> existing
                ));
    }

    // ④ 결제 예상 금액
    @Transactional(readOnly = true)
    public EstimateResponse estimate(List<OrderItemRequest> items) {
        long productAmount = 0;
        for (OrderItemRequest it : items) {
            ProductEntity p = em.find(ProductEntity.class, it.productId());
            if (p == null)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다: " + it.productId());
            int qty = (it.quantity() == null || it.quantity() < 1) ? 1 : it.quantity();
            productAmount += p.getPrice() * qty;
        }
        long discountAmount = 0, shippingFee = 0;
        return new EstimateResponse(productAmount, discountAmount, shippingFee,
                productAmount - discountAmount + shippingFee);
    }

    // ⑤ 주문 취소 (재고 복구 + 머니 환불)
    @Transactional
    public OrderCancelResponse cancelOrder(Long userId, Long orderId) {
        OrderEntity order = findMyOrder(userId, orderId);
        if (!"주문접수".equals(order.getStatus()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 배송이 시작되어 취소할 수 없습니다");

        List<OrderItemEntity> items = orderItemRepository.findByOrder_Id(orderId);
        for (OrderItemEntity oi : items) {
            oi.getProduct().increaseStock(oi.getQuantity());  // 재고 복구
            oi.changeStatus("취소됨");
        }
        order.getUser().addMoney(order.getTotalPrice());       // 머니 환불
        order.changeStatus("취소됨");

        return new OrderCancelResponse(order.getId(), "취소됨",
                order.getTotalPrice(), order.getUser().getAppMoney());
    }

    private OrderEntity findMyOrder(Long userId, Long orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "주문을 찾을 수 없습니다"));
        if (!order.getUser().getId().equals(userId))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인의 주문만 접근할 수 있습니다");
        return order;
    }
}