package com.models;

import com.dtos.EPaymentMethod;
import com.dtos.EStatusOrder;
import com.entities.OrderEntity;
import lombok.*;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderModel {
    private Long id;
    private String uuid;
    private Long addressId;
    private EPaymentMethod paymentMethod;
    private String note;
    private List<Long> orderDetailIds;

    public static OrderEntity toEntity(OrderModel model) {
        if (model == null) return null;
        return OrderEntity.builder()
                .id(model.getId())
                .uuid(model.getId() == null ? UUID.randomUUID().toString() : model.getUuid())
                .paymentMethod(model.paymentMethod.toString())
                .deliveryFee(Double.valueOf(30000f))
                .note(model.getNote())
                .status(EStatusOrder.PENDING.name())
                .build();
    }
}
