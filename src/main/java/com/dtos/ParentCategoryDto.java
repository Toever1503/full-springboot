package com.dtos;

import com.entities.CategoryEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ParentCategoryDto {
    private Long id;
    private String categoryName;
    private Integer deepLevel;
    private String parentCategorySlug;
    private ParentCategoryDto parentCategory;

    public static ParentCategoryDto toDto(CategoryEntity entity) {
        if (entity == null) return null;
        return ParentCategoryDto.builder()
                .id(entity.getId())
                .categoryName(entity.getCategoryName())
                .deepLevel(entity.getDeepLevel())
                .parentCategory(ParentCategoryDto.toDto(entity.getParentCategory()))
                .parentCategorySlug(entity.getSlug())
                .build();
    }
}
