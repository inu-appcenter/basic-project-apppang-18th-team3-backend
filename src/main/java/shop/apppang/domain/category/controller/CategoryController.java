package shop.apppang.domain.category.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.apppang.domain.category.dto.CategoryResponse;
import shop.apppang.domain.category.service.CategoryService;

@Tag(name = "카테고리")
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "대표 카테고리 목록 (아이콘 포함)")
    @ApiResponse(responseCode = "200", description = "카테고리 목록 (시드 데이터 10종)",
            content = @Content(schema = @Schema(implementation = CategoryResponse.class),
                    examples = @ExampleObject(value = """
                            {
                              "items": [
                                { "categoryId": 1, "name": "식품", "iconUrl": "/icons/category/food.png" },
                                { "categoryId": 2, "name": "생활용품", "iconUrl": "/icons/category/living.png" },
                                { "categoryId": 3, "name": "뷰티", "iconUrl": "/icons/category/beauty.png" },
                                { "categoryId": 4, "name": "의류 및 잡화", "iconUrl": "/icons/category/fashion.png" },
                                { "categoryId": 5, "name": "가전 및 디지털", "iconUrl": "/icons/category/digital.png" },
                                { "categoryId": 6, "name": "홈인테리어", "iconUrl": "/icons/category/interior.png" },
                                { "categoryId": 7, "name": "출산 및 유아", "iconUrl": "/icons/category/baby.png" },
                                { "categoryId": 8, "name": "반려동물", "iconUrl": "/icons/category/pet.png" },
                                { "categoryId": 9, "name": "스포츠 및 레저", "iconUrl": "/icons/category/sports.png" },
                                { "categoryId": 10, "name": "자동차용품", "iconUrl": "/icons/category/car.png" }
                              ]
                            }
                            """)))
    @GetMapping
    public CategoryResponse getCategories() {
        return categoryService.getCategories();
    }
}