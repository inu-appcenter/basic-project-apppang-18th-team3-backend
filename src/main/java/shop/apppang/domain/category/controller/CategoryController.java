package shop.apppang.domain.category.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.apppang.domain.category.dto.CategoryResponse;
import shop.apppang.domain.category.service.CategoryService;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "대표 카테고리 목록 (아이콘 포함)")
    @GetMapping
    public CategoryResponse getCategories() {
        return categoryService.getCategories();
    }
}