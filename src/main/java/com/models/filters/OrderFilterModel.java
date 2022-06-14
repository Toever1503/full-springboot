package com.models.filters;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderFilterModel {
    private String keyword;
    private String paymentMethod;
    private String status;
    private Date fromCreatedDate;
    private Date toCreatedDate;
    private Double fromTotalPrices;
    private Double toTotalPrices;
}
