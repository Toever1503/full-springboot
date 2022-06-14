package com.models.filters;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private Date fromCreatedDate;
    private Date toCreatedDate;
    @ApiModelProperty(notes = "User's name created", dataType = "String", example = "shiki")
    String createdBy;
}
