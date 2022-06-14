package com.models.specifications;

import com.entities.OrderEntity;
import com.entities.OrderEntity_;
import com.entities.UserEntity;
import com.entities.UserEntity_;
import com.models.filters.OrderFilterModel;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderSpecification extends BaseSpecification{
    // like user name method
    public static Specification<OrderEntity> likeUserName(String keyword) {
        return (root, query, criteriaBuilder) -> {
            Join<OrderEntity, UserEntity> createByJoin = root.join(OrderEntity_.createdBy);
            return criteriaBuilder.like(criteriaBuilder.upper(createByJoin.get(UserEntity_.userName)), "%" + keyword.toLowerCase() + "%");
        };
    }

    // filter by order date method
    public static Specification<OrderEntity> byOrderDate(Date fromCreateDate, Date toCreateDate ) {
        return (root, query, criteriaBuilder) -> {
            if (fromCreateDate != null && toCreateDate != null) {
                return criteriaBuilder.between(root.get(OrderEntity_.CREATED_DATE), fromCreateDate, toCreateDate);
            } else if (fromCreateDate != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get(OrderEntity_.CREATED_DATE), fromCreateDate);
            } else if (toCreateDate != null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get(OrderEntity_.CREATED_DATE), toCreateDate);
            } else {
                return null;
            }
        };
    }

    // filter by payment method
    public static Specification<OrderEntity> byOrderPaymentMethod(String nPaymentMethod) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(OrderEntity_.PAYMENT_METHOD), nPaymentMethod);
    }

    // filter by order status
    public static Specification<OrderEntity> byOrderStatus(String nStatus) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(OrderEntity_.STATUS), nStatus));
    }

    // filter by total price method
    public static Specification<OrderEntity> byTotalPrice(Double fromPrice, Double toPrice) {
        return (root, query, criteriaBuilder) -> {
            if (fromPrice != null && toPrice != null) {
                return criteriaBuilder.between(root.get(OrderEntity_.TOTAL_PRICES), fromPrice, toPrice);
            } else if (fromPrice != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get(OrderEntity_.TOTAL_PRICES), fromPrice);
            } else if (toPrice != null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get(OrderEntity_.TOTAL_PRICES), toPrice);
            } else {
                return null;
            }
        };
    }

    public static Specification<OrderEntity> filter(OrderFilterModel orderFilterModel){
        List<Specification<OrderEntity>> specs =new ArrayList<>();

        if(orderFilterModel.getKeyword() != null && !orderFilterModel.getKeyword().isEmpty()){
            specs.add(likeUserName(orderFilterModel.getKeyword()));
        }

        if(orderFilterModel.getPaymentMethod()!=null){
            specs.add(byOrderPaymentMethod(orderFilterModel.getPaymentMethod()));
        }
        if(orderFilterModel.getFromCreatedDate()!=null && orderFilterModel.getToCreatedDate()!=null){
            specs.add(byOrderDate(orderFilterModel.getFromCreatedDate(), orderFilterModel.getToCreatedDate()));
        }
        if (orderFilterModel.getStatus() != null) {
            specs.add(byOrderStatus(orderFilterModel.getStatus()));
        }
        if (orderFilterModel.getFromTotalPrices() != null && orderFilterModel.getToTotalPrices() != null) {
            specs.add(byTotalPrice(orderFilterModel.getFromTotalPrices(), orderFilterModel.getToTotalPrices()));
        }

        Specification<OrderEntity> finalSpec = null;
        for (Specification<OrderEntity> s : specs) {
            if (finalSpec == null) {
                finalSpec = s;
            } else
                finalSpec = finalSpec.and(s);
        }
        return finalSpec;
    }
}
