package com.dtos;

import com.entities.CategoryEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ParentCategoryDto {
    private Long id;
    private String categoryName;
    private ParentCategoryDto parentCategoryDto;

    public static ParentCategoryDto toDto(CategoryEntity entity) {
        if (entity == null) return null;
        return ParentCategoryDto.builder()
                .id(entity.getId())
                .categoryName(entity.getCategoryName())
                .parentCategoryDto(ParentCategoryDto.toDto(entity.getParentCategory()))
                .build();
    }
}
