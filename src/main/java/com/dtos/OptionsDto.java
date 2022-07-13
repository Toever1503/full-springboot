package com.dtos;

import com.entities.OptionsEntity;
import lombok.*;
import org.json.JSONObject;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class OptionsDto {
    private Long id;
    private String optionKey;
    private String optionValue;

   public static OptionsDto toDto(OptionsEntity entity){
       if(entity == null) return null;
       return OptionsDto.builder()
               .id(entity.getId())
               .optionKey(entity.getOptionKey())
               .optionValue(entity.getOptionValue())
               .build();
   }
}
