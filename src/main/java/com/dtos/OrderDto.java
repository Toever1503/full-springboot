package com.dtos;

import com.entities.OrderEntity;
import lombok.*;

import javax.persistence.Column;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class OrderDto {
    private Long id;
    private String uuid;
    private String paymentMethod;
    private String note;
    private Double totalPrices;
    private Integer totalNumberProducts;
    private String status;
    private String createdBy;
    private Date createdDate;
    private Date updatedDate;
    private String address;
    private String mainPhone;
    private String mainReceiver;
    private String deliveryCode;
    private Double deliveryFee;
    private List<OrderDetailDto> orderDetails;

    public static OrderDto toDto(OrderEntity entity) {
        if (entity == null) return null;
        return OrderDto.builder()
                .id(entity.getId())
                .address(entity.getMainAddress())
                .mainPhone(entity.getMainPhone())
                .mainReceiver(entity.getMainReceiver())
                .deliveryCode(entity.getDeliveryCode())
                .uuid(entity.getUuid())
                .paymentMethod(entity.getPaymentMethod())
                .note(entity.getNote())
                .deliveryFee(entity.getDeliveryFee())
                .totalPrices(entity.getTotalPrices())
                .totalNumberProducts(entity.getTotalNumberProducts())
                .status(entity.getStatus())
                .createdBy(entity.getCreatedBy() == null ? null : entity.getCreatedBy().getUserName())
                .createdDate(entity.getCreatedDate())
                .updatedDate(entity.getUpdatedDate())
                .orderDetails(entity.getOrderDetails() == null ? Collections.EMPTY_LIST : entity.getOrderDetails().stream().map(OrderDetailDto::toDto).collect(Collectors.toList()))
                .build();
    }
}
