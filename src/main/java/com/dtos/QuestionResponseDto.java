package com.dtos;

import lombok.*;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class QuestionResponseDto {
    private Long id;
    private String replyContent;
    private List<String> replyFiles;
    private Long userReply;
    private Date updatedDate;
    private StatusQuestion status;
}
