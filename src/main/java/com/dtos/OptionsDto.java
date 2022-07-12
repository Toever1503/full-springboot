package com.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.json.JSONObject;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OptionsDto {
    private Long id;
    private String optionKey;
    private String optionValue;

    public static JSONObject parseJson(String json){
        return new JSONObject(json);
    }
}
