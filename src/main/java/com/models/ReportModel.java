package com.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class ReportModel {
    private String status;
//    @JsonFormat(pattern = "yyyy-MM-DD HH:mm:ss")
    private Date time_from;
//    @JsonFormat(pattern = "yyyy-MM-DD HH:mm:ss")
    private Date time_to;
}
