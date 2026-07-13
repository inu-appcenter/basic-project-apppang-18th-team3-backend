package shop.apppang.domain.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shop.apppang.domain.category.dto.CategoryResponse;
import shop.apppang.domain.category.entity.CategoryEntity;
import shop.apppang.domain.category.repository.CategoryRepository;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    // 대표 카테고리 목록 조회
    public CategoryResponse getCategories() {

        List<CategoryEntity> categories = categoryRepository.findAllByOrderByDisplayOrderAsc();

        List<CategoryResponse.Item> items = categories.stream()
                .map(category -> new CategoryResponse.Item(
                        category.getId(),
                        category.getName(),
                        category.getIconUrl()
                ))
                .toList();
        return new CategoryResponse(items);
    }
}