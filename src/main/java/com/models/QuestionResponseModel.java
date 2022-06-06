package com.models;

import com.dtos.EStatusQuestion;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuestionResponseModel {
    @ApiModelProperty(notes = "Reply Content", dataType = "String", example = "Good question by you")
    @NotBlank
    @NotNull
    @Length(min = 1, max = 254)
    private String replyContent;
    @ApiModelProperty(notes = "Reply file", dataType = "Multipart file", example = "*.png, *.jpeg, *.mp4...")
    private List<MultipartFile> replyFile;
    private List<String> oldFiles = new ArrayList<String>();
    @ApiModelProperty(notes = "detail question link", dataType = "String", example = "http://traloioday.com/daxong")
    @NotNull
    @NotBlank
    private String url;
    private EStatusQuestion status;
}
