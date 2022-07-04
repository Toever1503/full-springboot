package com.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.repository.query.Param;

import javax.persistence.NamedStoredProcedureQuery;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureParameter;
import javax.persistence.Temporal;
import java.util.Date;
import java.util.List;


@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@NamedStoredProcedureQuery(name = "order_by_status_and_time", procedureName = "order_by_status_and_time", resultClasses = OrderByStatusAndTimeDto.class, parameters = {
        @StoredProcedureParameter(name = "status_order", mode = ParameterMode.IN, type = String.class),
        @StoredProcedureParameter(name = "time_from", mode = ParameterMode.IN, type = Date.class),
        @StoredProcedureParameter(name = "time_to", mode = ParameterMode.IN, type = Date.class),
        @StoredProcedureParameter(name = "hour_in_day", type = Integer.class, mode = ParameterMode.OUT),
        @StoredProcedureParameter(name = "total_order", type = Integer.class, mode = ParameterMode.OUT),
        @StoredProcedureParameter(name = "status_order", type = String.class, mode = ParameterMode.OUT),
        @StoredProcedureParameter(name = "total_products", type = Integer.class, mode = ParameterMode.OUT),
        @StoredProcedureParameter(name = "total_prices", type = Double.class, mode = ParameterMode.OUT),
        @StoredProcedureParameter(name = "order_date", type = Date.class, mode = ParameterMode.OUT),
})
public class OrderByStatusAndTimeDto {
    private Integer hour_in_day;
    private Integer total_order;
    private String status_order;
    private Integer total_products;
    private Double total_prices;
    private Date order_date;
}
