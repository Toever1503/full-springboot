package com.models.filters;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderFilterModel {
    private String uuid;
    private String username;
    private String address;
    private String note;
    private Double minTotalCost;
    private Double maxTotalCost;
    private Integer minTotalProducts;
    private Integer maxTotalProducts;
    private List<String> paymentMethods;
    private List<String> statusList;
    private Date minCreatedDate;
    private Date minUpdatedDate;
    private Date maxCreatedDate;
    private Date maxUpdatedDate;
}
