package shop.apppang.domain.chat.dto;

import java.util.List;

public record ChatResponse(
        String reply,
        String sessionId,
        List<RecommendedProduct> recommendedProducts
) {
    public record RecommendedProduct(
            Long productId,
            String name,
            String brand,
            Long price,
            String imageUrl
    ) {}
}
