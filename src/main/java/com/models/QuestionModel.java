package com.models;

import com.dtos.EQuestionCategory;
import com.entities.QuestionEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class QuestionModel {
    @ApiModelProperty(notes = "id uuid separate each question", dataType = "Long", example = "1")
    private Long id;

    @ApiModelProperty(notes = "category of question", dataType = "ENUM", example = "ORDERS")
    @NotNull
    private EQuestionCategory category;

    @ApiModelProperty(notes = "title of question", dataType = "String", example = "How to use Spring Boot?")
    @NotNull
    @NotBlank
    private String title;

    @ApiModelProperty(notes = "content of question", dataType = "String", example = "How to use Spring Boot?")
    @NotNull
    @NotBlank
    private String questContent;

    @ApiModelProperty(notes = "files of question", dataType = "List<MultipartFile>", example = "[\"file1\", \"file2\"]")
    private List<MultipartFile> questFile;

    @ApiModelProperty(notes = "List String of question origin", dataType = "List<String>", example = "file1, file2")
    private List<String> questOriginFile = new ArrayList<>();

    public static QuestionEntity toEntity(QuestionModel model) {
        if (model == null) throw new RuntimeException("QuestionModel is null");

        return QuestionEntity.builder()
                .id(model.getId())
                .category(model.getCategory().toString())
                .title(model.getTitle())
                .questContent(model.getQuestContent())
                .isCompatible(true)
                .build();
    }
}
