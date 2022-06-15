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
    private CategoryDto parentCategory;
    private List<CategoryDto> childCategories;

    public static CategoryDto toDto(CategoryEntity entity, boolean wantChild, boolean wantParent) {
        if (entity == null) return null;
        return CategoryDto.builder()
                .id(entity.getId())
                .categoryName(entity.getCategoryName())
                .slug(entity.getSlug())
                .description(entity.getDescription())
                .parentCategory(wantParent ? (entity.getParentCategory() == null ? null : CategoryDto.toDto(entity.getParentCategory(), wantChild, wantParent)) : null)
                .childCategories(wantChild ? (entity.getChildCategories().isEmpty() ? null : entity.getChildCategories().stream().map(child -> CategoryDto.toDto(child, wantChild, wantParent)).collect(Collectors.toList())) : null)
                .build();
    }
}
