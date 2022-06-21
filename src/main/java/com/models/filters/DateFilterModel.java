package com.models.filters;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class DateFilterModel {
    @JsonSerialize(as = Date.class)
    @JsonFormat(pattern = "yyyy-MM-DD HH:mm:ss")
    private Date minDate;
    @JsonSerialize(as = Date.class)
    @JsonFormat(pattern = "yyyy-MM-DD HH:mm:ss")
    private Date maxDate;
}
