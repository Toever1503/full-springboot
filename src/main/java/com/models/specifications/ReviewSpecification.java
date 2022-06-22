package com.models.specifications;

import com.entities.*;
import com.models.filters.ReviewFilterModel;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ReviewSpecification extends BaseSpecification{
    public static Specification likeCreatedBy(String createdBy) {
        return (root, query, cb) -> {
            Join<ReviewEntity, UserEntity> join = root.join(ReviewEntity_.createdBy);
            return cb.or(cb.like(join.get(UserEntity_.USER_NAME), "%" + createdBy + "%"), cb.like(join.get(UserEntity_.FULL_NAME), "%" + createdBy + "%"));
        };
    }

    public static Specification<ReviewEntity> filter(ReviewFilterModel filter) {
        List<Specification<ReviewEntity>> specs = new ArrayList<>();

        if (filter.getOptionName() != null)
            specs.add(like(ReviewEntity_.OPTION_NAME, filter.getOptionName()));

        if (filter.getContent() != null)
            specs.add(like(ReviewEntity_.CONTENT, filter.getContent()));

        if (filter.getRating() != null)
            specs.add(like(ReviewEntity_.RATING, filter.getRating()));

        if (filter.getStatus() != null)
            if (!filter.getStatus().isEmpty())
                specs.add(orIn(ReviewEntity_.STATUS, filter.getStatus().stream().map(c -> (Object) c).collect(Collectors.toList())));

        if (filter.getMinCreatedDate() != null && filter.getMaxCreatedDate() != null)
            specs.add(betweenDate(ReviewEntity_.CREATED_DATE, filter.getMinCreatedDate(), filter.getMaxCreatedDate()));
        else if (filter.getMinCreatedDate() != null)
            specs.add(dateGreaterThanEqual(ReviewEntity_.CREATED_DATE, filter.getMinCreatedDate()));
        else if (filter.getMaxUpdatedDate() != null)
            specs.add(dateLessThanEqual(ReviewEntity_.CREATED_DATE, filter.getMaxCreatedDate()));

        if (filter.getMinUpdatedDate() != null && filter.getMaxUpdatedDate() != null)
            specs.add(betweenDate(ReviewEntity_.UPDATED_DATE, filter.getMinUpdatedDate(), filter.getMaxUpdatedDate()));
        else if (filter.getMinUpdatedDate() != null)
            specs.add(dateGreaterThanEqual(ReviewEntity_.UPDATED_DATE, filter.getMinUpdatedDate()));
        else if (filter.getMaxUpdatedDate() != null)
            specs.add(dateLessThanEqual(ReviewEntity_.UPDATED_DATE, filter.getMaxUpdatedDate()));

        if (filter.getCreatedBy() != null)
            specs.add(likeCreatedBy(filter.getCreatedBy()));
        Specification finalSpec = null;

        for (Specification<ReviewEntity> spec : specs) {
            if (finalSpec == null)
                finalSpec = spec;
            else
                finalSpec = finalSpec.and(spec);
        }
        return finalSpec;
    }
}
