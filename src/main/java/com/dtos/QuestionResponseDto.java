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
    private QuestionDto questionDto;
    private String replyContent;
    private UserDto userReply;

    public static QuestionResponseDto toDto(QuestionEntity question) {
        return QuestionResponseDto
                .builder()
                .id(question.getId())
                .questionDto(QuestionDto.toDto(question))
                .replyContent(question.getReplyContent())
                .userReply(question.getAnsweredBy() != null ? UserDto.toDto(question.getAnsweredBy()) : null)
                .build();
    }
}
