package com.models.specifications;

import com.dtos.ECategoryType;
import com.entities.CategoryEntity_;
import org.springframework.data.jpa.domain.Specification;

public class CategorySpecification extends BaseSpecification {


    public static Specification byId(Long id) {
        return (root, query, cb) -> cb.equal(root.get(CategoryEntity_.ID), id);
    }

    public static Specification byType(ECategoryType type) {
        return (root, query, cb) -> cb.equal(root.get(CategoryEntity_.TYPE), type.name());
    }

}
