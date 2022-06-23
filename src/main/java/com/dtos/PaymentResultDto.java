package com.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResultDto {
    private Long amount;
    private String bankCode;
    private String bankTransNo;
    private String cardType;
    private String payDate;
    private String orderInfo;
    private String transactionNo;
    private String status;
    private String url;
}
