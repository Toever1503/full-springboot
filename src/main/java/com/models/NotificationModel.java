package com.models;

import com.dtos.ENotificationCategory;
import com.dtos.ENotificationStatus;
import com.entities.NotificationEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NotificationModel {
    @ApiModelProperty(notes = "Notification ID", dataType = "Long", example = "1")
    private Long id;
    @ApiModelProperty(notes = "image", dataType = "MultipartFile", example = "fileImage.jpg")
    private MultipartFile image;
    @ApiModelProperty(notes = "Notification category", dataType = "Enum", example = "SYSTEM, PROMOTION, EVENT")
    @NotNull
    private ENotificationCategory category;
    @ApiModelProperty(notes = "title", dataType = "String", example = "title")
    @NotNull
    @NotBlank
    private String title;
        @ApiModelProperty(notes = "content", dataType = "String", example = "content")
    @NotNull
    @NotBlank
    private String content;
    @ApiModelProperty(notes = "content Excerpt", dataType = "String", example = "content excerpt")
    private String contentExcerpt;
    @ApiModelProperty(notes = "attach Files detail", dataType = "List<MultipartFile>", example = "fileImage.jpg")
    private List<MultipartFile> attachFiles;
    @ApiModelProperty(notes = "List string link files old need keep", dataType = "List<String>", example = "[http://ijustforgotmypass.com, http://ijustforgotmypass1.com]")
    private List<String> attachFilesOrigin = new ArrayList<>();
    @ApiModelProperty(notes = "number of people viewed", dataType = "Integer", example = "1")
    private Integer viewed;
    @ApiModelProperty(notes = "Url point to notification detail", dataType = "String", example = "http://ijustansweredyourquestionhere.com")
    private String url;
    @ApiModelProperty(notes = "Notification is edit ?", dataType = "Boolean", example = "true")
    private Boolean isEdit;
    @ApiModelProperty(notes = "status Notification", dataType = "Enum", example = "PENDING, POSTED,FUTURE")
    @NotNull
    private ENotificationStatus status;
    @ApiModelProperty(notes = "future date if status notification is future", dataType = "Date", example = "2020-01-01")
    private Date futureDate;

    public static NotificationEntity toEntity(NotificationModel model) {
        if(model == null) return null;
        return NotificationEntity.builder()
                .id(model.getId())
                .category(model.getCategory().name())
                .title(model.getTitle())
                .content(model.getContent())
                .contentExcerpt(model.getContentExcerpt())
                .viewed(0)
                .url(model.getUrl())
                .isEdit(false)
                .countEdit(0)
                .status(model.getStatus().name())
                .futureDate(model.getFutureDate() == null ? null : new Date(model.getFutureDate().getTime()))
                .build();
    }
}
