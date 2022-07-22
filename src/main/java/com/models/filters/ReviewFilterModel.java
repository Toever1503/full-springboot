package com.models.filters;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewFilterModel {
    private Integer minReview;
    private Integer maxReview;
    private String optionName;
    private String content;
    private String rating;

    @ApiModelProperty(notes = "List status of review you want filter", dataType = "array", example = "PENDING, APPROVED")
    private List<String> status;

    @ApiModelProperty(notes = "Min Created Time of review", dataType = "datetime", example = "2022-06-10 09:18:56")
    @JsonSerialize(as = Date.class)
    @JsonFormat(pattern = "yyyy-MM-DD HH:mm:ss")
    private String minCreatedDate;
    @ApiModelProperty(notes = "Min Updated Time of review", dataType = "datetime", example = "2022-06-10 09:18:56")
    @JsonSerialize(as = Date.class)
    @JsonFormat(pattern = "yyyy-MM-DD HH:mm:ss")
    private String minUpdatedDate;
    @ApiModelProperty(notes = "Max Created Time of review", dataType = "datetime", example = "2022-06-10 09:18:56")
    @JsonSerialize(as = Date.class)
    @JsonFormat(pattern = "yyyy-MM-DD HH:mm:ss")
    private String maxCreatedDate;
    @ApiModelProperty(notes = "Max Updated Time of review", dataType = "datetime", example = "2022-06-10 09:18:56")
    @JsonSerialize(as = Date.class)
    @JsonFormat(pattern = "yyyy-MM-DD HH:mm:ss")
    private String maxUpdatedDate;

    @ApiModelProperty(notes = "User's name created", dataType = "String", example = "vudt")
    private String createdBy;
}
