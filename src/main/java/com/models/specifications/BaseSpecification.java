package com.models.specifications;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.Date;
import java.util.List;

public abstract class BaseSpecification {
    public static Specification like(String q, String field) {
        return (root, query, cb) -> cb.like(root.get(field), "%" + q + "%");
    }

    public static Specification betweenDate(String field, Date minDate, Date maxDate) {
        return (root, query, cb) -> cb.between(root.get(field), minDate, maxDate);
    }

    public static Specification in(String field, List<Object> vals) {
        return (root, query, cb) -> cb.in(root.get(field).in(vals));
    }

    public static Specification orIn(String field, List<Object> vals) {
        return (root, query, cb) -> cb.or(vals.stream().map(v -> cb.equal(root.get(field), v)).toArray(Predicate[]::new));
    }
}
