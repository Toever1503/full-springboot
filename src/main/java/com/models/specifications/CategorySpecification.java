package com.models.specifications;

import com.dtos.ECategoryType;
import com.entities.CategoryEntity_;
import org.springframework.data.jpa.domain.Specification;

public class CategorySpecification extends BaseSpecification {



    public static Specification byStatus(Boolean status){
        return (root, query, cb) -> cb.equal(root.get(CategoryEntity_.STATUS), status);
    }

}
