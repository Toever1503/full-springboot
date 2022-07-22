package com.models.specifications;

import com.entities.OrderEntity;
import com.entities.OrderEntity_;
import com.entities.UserEntity;
import com.entities.UserEntity_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public abstract class BaseSpecification {
    public static Specification like(String field, String q) {
        return (root, query, cb) -> cb.like(root.get(field), "%" + q + "%");
    }


    public static Specification equal(String field, Object data) {
        return (root, query, cb) -> cb.equal(root.get(field), data);
    }

    public static Specification betweenDate(String field, String minDate, String maxDate) throws ParseException {
        SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date1 = formatter1.parse(minDate);
        Date date2 = formatter2.parse(maxDate);
        return (root, query, cb) -> cb.between(root.get(field), date1, date2);
    }

    public static Specification dateGreaterThanEqual(String field, String data) throws ParseException {
        SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date1 = formatter1.parse(data);
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get(field), date1);
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

    public static Specification dateLessThanEqual(String field, String data) throws ParseException {
        SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date1 = formatter1.parse(data);
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get(field), date1);
    }

    public static Specification dateBetween(String field, String min, String max) throws ParseException {
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
