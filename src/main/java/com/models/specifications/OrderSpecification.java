package com.models.specifications;

import com.entities.*;
import com.models.filters.OrderFilterModel;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class OrderSpecification extends BaseSpecification {
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
    public static Specification<OrderEntity> byOrderDate(Date fromCreateDate, Date toCreateDate) {
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
    }

    ;

    // filter by order status
    public static Specification<OrderEntity> byOrderStatus(List<String> nStatus) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.or(
                nStatus.stream().map(status -> criteriaBuilder.equal(root.get(OrderEntity_.status), status)).toArray(Predicate[]::new)));
    }

    // filter by total price method


    public static Specification likeCreatedBy(String createdBy) {
        return (root, query, cb) -> {
            Join<OrderEntity, UserEntity> join = root.join(NotificationEntity_.createdBy);
            return cb.or(cb.like(join.get(UserEntity_.USER_NAME), createdBy), cb.like(join.get(UserEntity_.FULL_NAME), createdBy));
        };
    }

    public static Specification<OrderEntity> filter(OrderFilterModel orderFilterModel) {
        List<Specification<OrderEntity>> specs = new ArrayList<>();

        if (orderFilterModel.getUuid() != null)
            specs.add(like(OrderEntity_.UUID, orderFilterModel.getUuid()));

        if (orderFilterModel.getUsername() != null && !orderFilterModel.getUsername().isEmpty()) {
            specs.add(likeCreatedBy(orderFilterModel.getUsername()));
        }
        if (orderFilterModel.getAddress() != null && !orderFilterModel.getAddress().isEmpty()) {
            specs.add(like(OrderEntity_.MAIN_ADDRESS, orderFilterModel.getAddress()));
        }
        if (orderFilterModel.getNote() != null && !orderFilterModel.getNote().isEmpty()) {
            specs.add(likeNote(orderFilterModel.getNote()));
        }


        if (orderFilterModel.getMinCreatedDate() != null && orderFilterModel.getMaxCreatedDate() != null) {
            specs.add(betweenDate(OrderEntity_.CREATED_DATE, orderFilterModel.getMinCreatedDate(), orderFilterModel.getMaxCreatedDate()));
        } else if (orderFilterModel.getMinCreatedDate() != null) {
            specs.add(dateGreaterThanEqual(OrderEntity_.CREATED_DATE, orderFilterModel.getMinCreatedDate()));
        } else if (orderFilterModel.getMaxCreatedDate() != null) {
            specs.add(dateLessThanEqual(OrderEntity_.CREATED_DATE, orderFilterModel.getMaxCreatedDate()));
        }


        if (orderFilterModel.getMinUpdatedDate() != null && orderFilterModel.getMaxUpdatedDate() != null) {
            specs.add(betweenDate(OrderEntity_.UPDATED_DATE, orderFilterModel.getMinUpdatedDate(), orderFilterModel.getMaxUpdatedDate()));
        } else if (orderFilterModel.getMinCreatedDate() != null) {
            specs.add(dateGreaterThanEqual(OrderEntity_.UPDATED_DATE, orderFilterModel.getMinUpdatedDate()));
        } else if (orderFilterModel.getMaxCreatedDate() != null) {
            specs.add(dateLessThanEqual(OrderEntity_.UPDATED_DATE, orderFilterModel.getMaxUpdatedDate()));
        }

        if (orderFilterModel.getStatusList() != null) {
            specs.add(orIn(OrderEntity_.STATUS, orderFilterModel.getStatusList().stream().map(e -> (Object) e).collect(Collectors.toList())));
        }
        if (orderFilterModel.getPaymentMethods() != null) {
            specs.add(orIn(OrderEntity_.PAYMENT_METHOD, orderFilterModel.getPaymentMethods().stream().map(e -> (Object) e).collect(Collectors.toList())));
        }

        if (orderFilterModel.getMinTotalCost() != null || orderFilterModel.getMaxTotalCost() != null)
            specs.add(byBetweenTypeDouble(OrderEntity_.TOTAL_PRICES, orderFilterModel.getMinTotalCost(), orderFilterModel.getMaxTotalCost()));
        if (orderFilterModel.getMinTotalProducts() != null || orderFilterModel.getMaxTotalProducts() != null)
            specs.add(byBetweenTypeInteger(OrderEntity_.TOTAL_NUMBER_PRODUCTS, orderFilterModel.getMinTotalProducts(), orderFilterModel.getMaxTotalProducts()));

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
