package com.models.filters;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class DateFilterModel {
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date minDate;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date maxDate;
}
