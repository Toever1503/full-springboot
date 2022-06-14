package com.models.filters;

import com.dtos.EStatusQuestion;
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
public class QuestionFilterModel {
    @ApiModelProperty(notes = "List status of question you want filter", dataType = "String", example = "PENDING")
    private List<String> categories;

    @ApiModelProperty(notes = "Search title which contains", dataType = "String", example = "a")
    private String title;

    @ApiModelProperty(notes = "Search content which contains", dataType = "String", example = "a")
    private String questContent;

    @ApiModelProperty(notes = "Search content which contains", dataType = "String", example = "a")
    private String replyContent;
    @ApiModelProperty(notes = "List status of question you want filter", dataType = "array", example = "SHIPPING")
    private List<String> status;

    @ApiModelProperty(notes = "Min Created Time of question", dataType = "datetime", example = "2022-06-10 09:18:56")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date minCreatedDate;
    @ApiModelProperty(notes = "Min Updated Time of question", dataType = "datetime", example = "2022-06-10 09:18:56")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date minUpdatedDate;
    @ApiModelProperty(notes = "Max Created Time of question", dataType = "datetime", example = "2022-06-10 09:18:56")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date maxCreatedDate;
    @ApiModelProperty(notes = "Max Updated Time of question", dataType = "datetime", example = "2022-06-10 09:18:56")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date maxUpdatedDate;
    @ApiModelProperty(notes = "User's name created", dataType = "String", example = "shiki")
    String createdBy;

}
