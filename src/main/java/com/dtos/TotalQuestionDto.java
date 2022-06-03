package com.dtos;

import com.entities.QuestionEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private List<String> questFiles;
    private Date createdDate;
    private Date updatedDate;
    private EStatusQuestion status;
    private String replyContent;
    private List<String> replyFiles;
    private String userReply;

    public static TotalQuestionDto toTotalQuestionDTO(QuestionEntity entity){
            if(entity==null) throw new RuntimeException("No Question Found!");
            TotalQuestionDto dto = new TotalQuestionDto();
            dto.setId(entity.getId());
            dto.setStatus(EStatusQuestion.valueOf(entity.getStatus()));
            dto.setCreatedDate(entity.getCreatedDate());
            dto.setQuestContent(entity.getQuestContent());
            dto.setTitle(entity.getTitle());
            dto.setQuestFiles(Collections.singletonList(entity.getQuestContent()));
            dto.setUpdatedDate(entity.getUpdatedDate());
            dto.setReplyFiles(Collections.singletonList(entity.getReplyFile()));
            dto.setReplyContent(entity.getReplyContent());
            dto.setUserReply(entity.getAnsweredBy().getFullName());
            return dto;
    }
}
