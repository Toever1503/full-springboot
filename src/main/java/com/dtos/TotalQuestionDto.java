package com.dtos;

import com.entities.QuestionEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TotalQuestionDto {
    private Long id;
    private String title;
    private String questContent;
    private List<Object> questFiles;
    private Date createdDate;
    private Date updatedDate;
    private EStatusQuestion status;
    private String replyContent;
    private List<Object> replyFiles;
    private String userReply;

    public static TotalQuestionDto toTotalQuestionDTO(QuestionEntity entity){
            if(entity==null) throw new RuntimeException("No Question Found!");
            TotalQuestionDto dto = new TotalQuestionDto();
            dto.setId(entity.getId());
            dto.setStatus(EStatusQuestion.valueOf(entity.getStatus()));
            dto.setCreatedDate(entity.getCreatedDate());
            dto.setQuestContent(entity.getQuestContent());
            dto.setTitle(entity.getTitle());
            dto.setQuestFiles((entity.getQuestFile() == null ? null : parseJson(entity.getQuestFile()).getJSONArray("files").toList()));
            dto.setUpdatedDate(entity.getUpdatedDate());
            dto.setReplyFiles((entity.getReplyFile() == null ? null : parseJson(entity.getReplyFile()).getJSONArray("files").toList()));
            dto.setReplyContent(entity.getReplyContent());
            dto.setUserReply(entity.getAnsweredBy().getFullName());
            return dto;


    }
    public static JSONObject parseJson(String json){
        System.out.println("json: " + json);
        return new JSONObject(json);
    }
}
