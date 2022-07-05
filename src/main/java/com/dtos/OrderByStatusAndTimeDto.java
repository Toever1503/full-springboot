package com.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class OrderByStatusAndTimeDto {
    private Integer hour_in_day;
    private Integer total_order;
    private String status_order;
    private Integer total_products;
    private Double total_prices;
    private LocalDateTime order_date;
}
