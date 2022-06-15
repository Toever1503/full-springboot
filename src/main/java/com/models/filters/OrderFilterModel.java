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
    private String userName;
    private String address;
    private String note;
    private List<String> paymentMethods;
    private List<String> statusList;
    private Date fromCreatedDate;
    private Date toCreatedDate;
    private Double minTotalPrices;
    private Double maxTotalPrices;
}
