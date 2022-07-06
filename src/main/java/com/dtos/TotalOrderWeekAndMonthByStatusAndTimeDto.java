package com.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class TotalOrderWeekAndMonthByStatusAndTimeDto {
    private Integer total_order;
    private String status_order;
    private Integer total_products;
    private Double total_prices;
}
