package com.models;

import com.entities.CategoryEntity;
import com.utils.ASCIIConverter;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class CategoryModel {
    @ApiModelProperty(notes = "Category ID", dataType = "Long", example = "1")
    private Long id;

    @ApiModelProperty(notes = "Category name", dataType = "String", example = "smart phone samsung A20")
    @NotBlank
    @NotNull
    private String categoryName;

    @ApiModelProperty(notes = "slug name, can be automatically generated based on the category name or can be entered manually if desired", dataType = "String", example = "smart-phone-samsung-A20")
    private String slug;

    @ApiModelProperty(notes = "description category", dataType = "String", example = "this is descripton for category")
    private String description;

    @ApiModelProperty(notes = "Category Id parent", dataType = "Long", example = "1")
    private Long parentId;
    @ApiModelProperty(notes = "Industry Id", dataType = "Long", example = "1")
    private Long industryId;

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
