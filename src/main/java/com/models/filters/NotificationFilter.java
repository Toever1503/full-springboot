package com.models.filters;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
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

    @JsonSerialize(as = Date.class)
    @JsonFormat(pattern = "yyyy-MM-DD HH:mm:ss")
    private String minCreatedDate;
    @JsonSerialize(as = Date.class)
    @JsonFormat(pattern = "yyyy-MM-DD HH:mm:ss")
    private String maxCreatedDate;
    @ApiModelProperty(notes = "User's name created", dataType = "String", example = "shiki")
    String createdBy;
}
