package com.models.specifications;

import com.entities.OrderEntity;
import com.entities.OrderEntity_;
import com.entities.UserEntity;
import com.entities.UserEntity_;
import com.models.filters.OrderFilterModel;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderSpecification extends BaseSpecification{
    // like user name method
    public static Specification<OrderEntity> likeUserName(String cratedBy) {
        return (root, query, criteriaBuilder) -> {
            Join<OrderEntity, UserEntity> createByJoin = root.join(OrderEntity_.createdBy);
            return criteriaBuilder.like(criteriaBuilder.upper(createByJoin.get(UserEntity_.userName)), "%" + cratedBy.toUpperCase() + "%");
        };
    }

    //like address method
    public static Specification<OrderEntity> likeAddress(String address) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(OrderEntity_.MAIN_ADDRESS), "%" + address + "%");
    }

    // like note method
    public static Specification<OrderEntity> likeNote(String note) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(OrderEntity_.NOTE), "%" + note + "%");
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
    public static Specification<OrderEntity> byOrderPaymentMethod(List<String> nPaymentMethods) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.or(
                    nPaymentMethods.stream().map(paymentMethod -> criteriaBuilder.equal(root.get(OrderEntity_.paymentMethod), paymentMethod)).toArray(Predicate[]::new));
        };

    // filter by order status
    public static Specification<OrderEntity> byOrderStatus(List<String> nStatus) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.or(
                    nStatus.stream().map(status -> criteriaBuilder.equal(root.get(OrderEntity_.status), status)).toArray(Predicate[]::new)));
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

        if(orderFilterModel.getUserName() != null && !orderFilterModel.getUserName().isEmpty()){
            specs.add(likeUserName(orderFilterModel.getUserName()));
        }
        if(orderFilterModel.getAddress() != null && !orderFilterModel.getAddress().isEmpty()){
            specs.add(likeAddress(orderFilterModel.getAddress()));
        }
        if(orderFilterModel.getNote() != null && !orderFilterModel.getNote().isEmpty()){
            specs.add(likeNote(orderFilterModel.getNote()));
        }

        if(orderFilterModel.getPaymentMethods()!=null){
            specs.add(byOrderPaymentMethod(orderFilterModel.getPaymentMethods()));
        }
        if(orderFilterModel.getFromCreatedDate()!=null || orderFilterModel.getToCreatedDate()!=null){
            specs.add(byOrderDate(orderFilterModel.getFromCreatedDate(), orderFilterModel.getToCreatedDate()));
        }
        if (orderFilterModel.getStatusList() != null) {
            specs.add(byOrderStatus(orderFilterModel.getStatusList()));
        }
        if (orderFilterModel.getMinTotalPrices() != null || orderFilterModel.getMaxTotalPrices() != null) {
            specs.add(byTotalPrice(orderFilterModel.getMinTotalPrices(), orderFilterModel.getMaxTotalPrices()));
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
