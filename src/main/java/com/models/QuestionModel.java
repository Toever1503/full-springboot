package com.models;

import com.dtos.StatusQuestion;
import com.entities.QuestionEntity;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class QuestionModel {
    private Long id;
    private String title;
    private String questContent;
    private List<MultipartFile> questFile;
    private List<String> questOriginFile;
    private Long createById;

    public static QuestionEntity toEntity(QuestionModel model) {
        if (model == null) throw new RuntimeException("QuestionModel is null");

        return QuestionEntity.builder()
                .id(model.getId())
                .title(model.getTitle())
                .questContent(model.getQuestContent())
                .isCompatible(true)
                .build();
    }
}
