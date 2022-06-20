package com.models.filters;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date minCreatedDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date minUpdatedDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date maxCreatedDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date maxUpdatedDate;
}
