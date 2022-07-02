package com.dtos;

import com.entities.TagEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class TagDto {
    private Long id;

    @Field(type = FieldType.Keyword)
    private String tagName;

    public static TagDto toTagDto(TagEntity entity) {
        if (entity == null) return null;
        TagDto sample = new TagDto();
        sample.setTagName(entity.getTagName());
        sample.setId(entity.getId());
        return sample;
    }
}
