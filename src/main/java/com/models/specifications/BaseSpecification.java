package com.models.specifications;

import com.entities.OrderEntity;
import com.entities.OrderEntity_;
import com.entities.UserEntity;
import com.entities.UserEntity_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.util.Date;
import java.util.List;

public abstract class BaseSpecification {
    public static Specification like(String q, String field) {
        return (root, query, cb) -> cb.like(root.get(field), "%" + q + "%");
    }


    public static Specification equal(String field, Object data) {
        return (root, query, cb) -> cb.equal(root.get(field), data);
    }

    public static Specification betweenDate(String field, Date minDate, Date maxDate) {
        return (root, query, cb) -> cb.between(root.get(field), minDate, maxDate);
    }

    public static Specification dateGreaterThanEqual(String field, Date data) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get(field), data);
    }

    public static Specification<OrderEntity> byBetweenTypeDouble(String field, Double min, Double max) {
        return (root, query, criteriaBuilder) -> {
            if (min != null && max != null) {
                return criteriaBuilder.between(root.get(field), min, max);
            } else if (min != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get(field), min);
            } else if (max != null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get(field), max);
            } else {
                return null;
            }
        };
    }

    public static Specification<OrderEntity> byBetweenTypeInteger(String field, Integer min, Integer max) {
        return (root, query, criteriaBuilder) -> {
            if (min != null && max != null) {
                return criteriaBuilder.between(root.get(field), min, max);
            } else if (min != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get(field), min);
            } else if (max != null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get(field), max);
            } else {
                return null;
            }
        };
    }

    public static Specification dateLessThanEqual(String field, Date data) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get(field), data);
    }

    public static Specification dateBetween(String field, Date min, Date max) {
        if (min != null && max != null)
            return betweenDate(field, min, max);
        else if (min != null)
            return dateGreaterThanEqual(field, min);
        else if (max != null)
            return dateLessThanEqual(field, max);
        else return null;
    }

    public static Specification in(String field, List<Object> vals) {
        return (root, query, cb) -> cb.in(root.get(field).in(vals));
    }

    public static Specification orIn(String field, List<Object> vals) {
        return (root, query, cb) -> cb.or(vals.stream().map(v -> cb.equal(root.get(field), v)).toArray(Predicate[]::new));
    }
}
