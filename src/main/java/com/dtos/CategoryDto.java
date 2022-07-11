package com.dtos;

import com.entities.CategoryEntity;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;
import java.util.stream.Collectors;

import static com.dtos.QuestionDto.parseJson;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class CategoryDto {
    private Long id;
    private String categoryName;

    @Field(type = FieldType.Keyword, name = "categorySLug")
    private String slug;

    private String description;

    private ParentCategoryDto parentCategory;

    private List<CategoryDto> childCategories;

    private Integer deepLevel;

    private String image;

    private Boolean status;

    public static CategoryDto toDto(CategoryEntity entity, boolean wantChild) {
        if (entity == null) return null;
        return CategoryDto.builder()
                .id(entity.getId())
                .categoryName(entity.getCategoryName())
                .slug(entity.getSlug())
                .description(entity.getDescription())
                .deepLevel(entity.getDeepLevel())
                .parentCategory(ParentCategoryDto.toDto(entity.getParentCategory()))
                .childCategories(wantChild == true ? (entity.getChildCategories() == null ? null : entity.getChildCategories().stream().map(child -> CategoryDto.toDto(child, wantChild)).collect(Collectors.toList())) : null)
                .status(entity.getStatus())
                .image(entity.getCatFile())
                .build();
    }
}
