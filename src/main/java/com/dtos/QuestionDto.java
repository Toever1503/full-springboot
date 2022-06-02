package com.dtos;

import com.entities.QuestionEntity;
import lombok.*;
import org.json.JSONObject;

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
                .title(questionEntity.getTitle())
                .questContent(questionEntity.getQuestContent())
                .questFiles(questionEntity.getQuestFile() != null ? parseJson(questionEntity.getQuestFile()).getJSONArray("files").toList() : null)
                .createdBy(UserDto.toDto(questionEntity.getCreatedBy()))
                .createdDate(questionEntity.getCreatedDate())
                .updatedDate(questionEntity.getUpdatedDate())
                .status(questionEntity.getStatus())
                .build();
    }

        public static JSONObject parseJson(String json){
            return new JSONObject(json);
        }

//    public static void main(String[] args) {
//        List<String> listString = List.of("1", "2", "3");
//        // convert list to json, after save to db
//        System.out.println(new JSONObject(Map.of("files", listString)));
//        // convert json to list, after get from db
//        System.out.println(new JSONObject("{\"files\":[\"1\",\"2\",\"3\"]}").getJSONArray("files").toList());
//    }
}
