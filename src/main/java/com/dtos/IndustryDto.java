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
public class IndustryDto {
    private Long id;
    private String industryName;
    private String slug;
    private String description;
    private List<CategoryDto> childCategories;

    public static IndustryDto toDto(CategoryEntity entity, boolean wantChild) {
        if (entity == null) return null;
        return IndustryDto.builder()
                .id(entity.getId())
                .industryName(entity.getCategoryName())
                .slug(entity.getSlug())
                .description(entity.getDescription())
                .childCategories(wantChild == true ? (entity.getChildCategories() == null ? null : entity.getChildCategories().stream().map(child -> CategoryDto.toDto(child, wantChild)).collect(Collectors.toList())) : null)
                .build();
    }
}
