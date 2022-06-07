package com.dtos;

import com.entities.CategoryEntity;
import lombok.*;

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
    private String parentCategory;

    public static CategoryDto toDto(CategoryEntity entity) {
        if(entity == null) return null;
        return CategoryDto.builder()
                .id(entity.getId())
                .categoryName(entity.getCategoryName())
                .slug(entity.getSlug())
                .description(entity.getDescription())
                .parentCategory(entity.getParentCategory() == null ? null : entity.getParentCategory().getCategoryName())
                .build();
    }
}
