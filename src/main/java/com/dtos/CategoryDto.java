package com.dtos;

import com.entities.CategoryEntity;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class CategoryDto {
    private Long id;
    private String categoryName;
    private String slug;
    private String description;
    private ParentCategoryDto parentCategory;
    private List<CategoryDto> childCategories;

    public static CategoryDto toDto(CategoryEntity entity, boolean wantChild) {
        if (entity == null) return null;
        return CategoryDto.builder()
                .id(entity.getId())
                .categoryName(entity.getCategoryName())
                .slug(entity.getSlug())
                .description(entity.getDescription())
                .parentCategory(entity.getParentCategory() == null ? null : ParentCategoryDto.toDto(entity.getParentCategory()))
                .childCategories(wantChild == true ? (entity.getChildCategories().isEmpty() ? null : entity.getChildCategories().stream().map(child -> CategoryDto.toDto(child, wantChild)).collect(Collectors.toList())) : null)
                .build();
    }
}
