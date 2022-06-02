package com.dtos;

import com.entities.QuestionEntity;
import lombok.*;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class QuestionDto {
    private Long id;
    private String category;
    private String title;
    private String questContent;
    private List<Object> questFiles;
    private UserDto createdBy;
    private Date createdDate;
    private Date updatedDate;
    private String status;

    public static QuestionDto toDto(QuestionEntity questionEntity) {
        return QuestionDto.builder()
                .id(questionEntity.getId())
                .category(questionEntity.getCategory())
                .title(questionEntity.getTitle())
                .questContent(questionEntity.getQuestContent())
                .questFiles(questionEntity.getQuestFile() == null ? null : parseJson(questionEntity.getQuestFile()).getJSONArray("files").toList())
                .createdBy(UserDto.toDto(questionEntity.getCreatedBy()))
                .createdDate(questionEntity.getCreatedDate())
                .updatedDate(questionEntity.getUpdatedDate())
                .status(questionEntity.getStatus())
                .build();
    }

        public static JSONObject parseJson(String json){
            System.out.println("json: " + json);
            return new JSONObject(json);
        }

}
