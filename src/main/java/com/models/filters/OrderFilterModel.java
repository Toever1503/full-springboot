package com.models.filters;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderFilterModel {
    private String username;
    private String address;
    private String note;
    private Double minTotalCost;
    private Double maxTotalCost;
    private Integer minTotalProducts;
    private Integer maxTotalProducts;
    private List<String> paymentMethods;
    private List<String> statusList;
    @JsonSerialize(as = Date.class)
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private Date minCreatedDate;
    @JsonSerialize(as = Date.class)
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private Date minUpdatedDate;
    @JsonSerialize(as = Date.class)
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private Date maxCreatedDate;
    @JsonSerialize(as = Date.class)
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private Date maxUpdatedDate;
}
