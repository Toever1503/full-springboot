package com.dtos;

import com.entities.QuestionEntity;
import lombok.*;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class QuestionResponseDto {
    private Long id;
    private String category;
    private String replyContent;
    private List<Object> replyFiles;
    private UserDto userReply;
    private Date updatedDate;
    private EStatusQuestion status;

    public static QuestionResponseDto fromEntity(QuestionEntity question) {
        return QuestionResponseDto
                .builder()
                .id(question.getId())
                .category(question.getCategory())
                .replyContent(question.getReplyContent())
                .userReply(UserDto.toDto(question.getAnsweredBy()))
                .updatedDate(question.getUpdatedDate())
                .replyFiles(question.getReplyFile() != null ? new JSONObject(question.getReplyFile()).getJSONArray("files").toList() : null)
                .status(EStatusQuestion.COMPLETED)
                .build();
    }
}
