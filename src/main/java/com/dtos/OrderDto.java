package com.dtos;

import com.entities.OrderEntity;
import lombok.*;

import javax.persistence.Column;
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
    private AddressDto address;
    private String paymentMethod;
    private String note;
    private Double totalPrices;
    private Integer totalNumberProducts;
    private String status;
    private UserDto createdBy;
    private Date createdDate;
    private Date updatedDate;

    private String mainAddress;
    private String mainPhone;
    private String mainReceiver;
    private String COD;

    private List<OrderDetailDto> orderDetails;

    public static OrderDto toDto(OrderEntity entity) {
        if(entity == null) return null;
        return OrderDto.builder()
                .id(entity.getId())
                .mainAddress(entity.getMainAddress())
                .mainPhone(entity.getMainPhone())
                .mainReceiver(entity.getMainReceiver())
                .COD(entity.getCOD())
                .uuid(entity.getUuid())
                .address(AddressDto.toDto(entity.getAddress()))
                .paymentMethod(entity.getPaymentMethod())
                .note(entity.getNote())
                .totalPrices(entity.getTotalPrices())
                .totalNumberProducts(entity.getTotalNumberProducts())
                .status(entity.getStatus())
                .createdBy(entity.getCreatedBy() == null ? null : UserDto.toDto(entity.getCreatedBy()))
                .createdDate(entity.getCreatedDate())
                .updatedDate(entity.getUpdatedDate())
                .orderDetails(entity.getOrderDetails() == null ? null : entity.getOrderDetails().stream().map(OrderDetailDto::toDto).collect(Collectors.toList()))
                .build();
    }
}
