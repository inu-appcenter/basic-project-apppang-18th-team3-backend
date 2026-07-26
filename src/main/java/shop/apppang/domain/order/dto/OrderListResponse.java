package shop.apppang.domain.order.dto;

import java.util.List;

// 주문 목록 응답 wrapper.
// 목록(items)만 내려주면 프론트가 "다음 페이지가 있는지", "전체가 몇 개인지" 알 수 없으므로
// 페이징 메타 정보를 함께 담아 내려준다. (상품 목록 ProductListResponse와 동일한 구조)
public record OrderListResponse(
        Integer page,        // 현재 페이지 번호 (프론트 기준 1-based)
        Integer size,        // 페이지당 개수
        Integer total,       // 조건에 맞는 전체 주문 수
        Boolean hasNext,     // 다음 페이지 존재 여부
        List<OrderSummaryResponse> items
) {}
