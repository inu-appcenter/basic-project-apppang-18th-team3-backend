package shop.apppang.domain.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 결제 예상 금액 조회(GET)용 쿼리 바인딩 객체.
 * items[0].productId=5&items[0].quantity=2 형태의 인덱스 파라미터를 @ModelAttribute로 바인딩한다.
 * 인덱스 바인딩은 요소를 no-arg 생성자 + setter로 생성하므로 record가 아니라 mutable 클래스로 둔다.
 * (JSON 바디용 OrderItemRequest record와는 별도)
 */
@Getter
@Setter
@NoArgsConstructor
public class EstimateRequest {

    @Schema(description = "예상 금액을 계산할 상품 목록")
    private List<Item> items = new ArrayList<>();

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Item {
        @Schema(description = "상품 ID", example = "5")
        private Long productId;

        @Schema(description = "수량", example = "2")
        private Integer quantity;
    }
}
