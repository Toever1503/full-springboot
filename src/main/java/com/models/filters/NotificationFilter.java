package com.models.filters;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class NotificationFilter {
    private String title;
    private String content;
    private String contentExcerpt;
    private List<String> status;
    private Integer minViewed;
    private Integer maxViewed;
    private List<String> category;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date fromCreatedDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date toCreatedDate;
    @ApiModelProperty(notes = "User's name created", dataType = "String", example = "shiki")
    String createdBy;
}
