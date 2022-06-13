package com.models.filters;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

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
    private String sex;

    @ApiModelProperty(notes = "Max birthday of user", dataType = "datetime", example = "2022-06-10 09:18:56")
    private Date minBirthDay;
    @ApiModelProperty(notes = "Max birthday of user", dataType = "datetime", example = "2022-06-10 09:18:56")
    private Date maxBirthDay;

    @ApiModelProperty(notes = "Min Created Time of user", dataType = "datetime", example = "2022-06-10 09:18:56")
    private Date minCreatedDate;
    @ApiModelProperty(notes = "Max Created Time of user", dataType = "datetime", example = "2022-06-10 09:18:56")
    private Date maxCreatedDate;

    @ApiModelProperty(notes = "Min Updated Time of user", dataType = "datetime", example = "2022-06-10 09:18:56")
    private Date minUpdatedDate;
    @ApiModelProperty(notes = "Max Updated Time of user", dataType = "datetime", example = "2022-06-10 09:18:56")
    private Date maxUpdatedDate;
}
