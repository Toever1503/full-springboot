package com.dtos;

import com.entities.TagEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class TagDto {
    private Long id;
    private String tagName;
    private String slug;

    public static TagDto toTagDto(TagEntity entity){
        if(entity==null) throw new RuntimeException("tagEntity is null!");
        TagDto sample = new TagDto();
        sample.setTagName(entity.getTagName());
        sample.setSlug(entity.getSlug());
        sample.setId(entity.getId());
        return sample;
    }
}
