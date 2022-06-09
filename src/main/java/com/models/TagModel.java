package com.models;

import com.entities.TagEntity;
import com.utils.ASCIIConverter;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagModel {
    private Long id;
    @NotNull
    @NotBlank
    private String tagName;
    private String slug;

    public static TagEntity toEntity(TagModel model) {
        if(model == null) new RuntimeException("TagModel is null");
        return TagEntity.builder()
                .id(model.getId())
                .tagName(model.getTagName())
                .slug(model.getSlug() == null ? ASCIIConverter.utf8ToAscii(model.getTagName()) : ASCIIConverter.utf8ToAscii(model.getSlug()))
                .build();
    }
}
