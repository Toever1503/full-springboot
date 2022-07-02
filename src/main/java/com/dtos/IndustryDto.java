package com.dtos;

import com.entities.CategoryEntity;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

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

    @Field(type = FieldType.Keyword, name = "industrySLug")
    private String slug;

    private String description;

    public static IndustryDto toDto(CategoryEntity entity) {
        if (entity == null) return null;
        return IndustryDto.builder()
                .id(entity.getId())
                .industryName(entity.getCategoryName())
                .slug(entity.getSlug())
                .description(entity.getDescription())
                .build();
    }
}
