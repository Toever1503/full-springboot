package com.models;

import com.dtos.ETypeCategory;
import com.entities.CategoryEntity;
import com.utils.ASCIIConverter;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class CategoryModel {
    private Long id;
    private ETypeCategory type;
    private String categoryName;
    private String slug;
    private String description;
    private Long parentId;

    public static CategoryEntity toEntity(CategoryModel model) {
        if(model == null) return null;
        return CategoryEntity.builder()
                .id(model.getId())
                .categoryName(model.getCategoryName())
                .slug(model.getSlug() == null ? ASCIIConverter.utf8ToAscii(model.getCategoryName()) : ASCIIConverter.utf8ToAscii(model.getSlug()))
                .description(model.getDescription())
                .totalProduct(0L)
                .build();
    }
}
