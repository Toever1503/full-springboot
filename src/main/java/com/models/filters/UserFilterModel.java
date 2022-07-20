package com.models.filters;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserFilterModel {
    @ApiModelProperty(notes = "Search username which contains", dataType = "String", example = "a")
    private String username;
    @ApiModelProperty(notes = "Search fullName which contains", dataType = "String", example = "a")
    private String fullname;
    @ApiModelProperty(notes = "Search email which contains", dataType = "String", example = "a")
    private String email;
    @ApiModelProperty(notes = "Search phone which contains", dataType = "String", example = "a")
    private String phone;
    @ApiModelProperty(notes = "Search sex which contains", dataType = "String", example = "a")
    private List<String> sex;

    @ApiModelProperty(notes = "Search status which contains", dataType = "String", example = "a")
    private Boolean status;

    @ApiModelProperty(notes = "Search lock status which contains", dataType = "String", example = "a")
    private Boolean lockStatus;

    @ApiModelProperty(notes = "Max birthday of user", dataType = "datetime", example = "2022-06-10 09:18:56")
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date minBirthDay;
    @ApiModelProperty(notes = "Max birthday of user", dataType = "datetime", example = "2022-06-10 09:18:56")
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date maxBirthDay;

    @ApiModelProperty(notes = "Min Created Time of user", dataType = "datetime", example = "2022-06-10 09:18:56")
    @JsonFormat(pattern = "yyyy-MM-DD HH:mm:ss")
    private Date minCreatedDate;
    @ApiModelProperty(notes = "Max Created Time of user", dataType = "datetime", example = "2022-06-10 09:18:56")
    @JsonFormat(pattern = "yyyy-MM-DD HH:mm:ss")
    private Date maxCreatedDate;

    @ApiModelProperty(notes = "Min Updated Time of user", dataType = "datetime", example = "2022-06-10 09:18:56")
    @JsonFormat(pattern = "yyyy-MM-DD HH:mm:ss")
    private Date minUpdatedDate;
    @ApiModelProperty(notes = "Max Updated Time of user", dataType = "datetime", example = "2022-06-10 09:18:56")
    @JsonFormat(pattern = "yyyy-MM-DD HH:mm:ss")
    private Date maxUpdatedDate;
}
