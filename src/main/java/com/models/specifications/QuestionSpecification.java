package com.models.specifications;

import com.entities.QuestionEntity;
import com.entities.QuestionEntity_;
import com.entities.UserEntity;
import com.entities.UserEntity_;
import com.models.filters.QuestionFilterModel;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class QuestionSpecification extends BaseSpecification {

    public static Specification likeCreatedBy(String createdBy) {
        return (root, query, cb) -> {
            Join<QuestionEntity, UserEntity> join = root.join(QuestionEntity_.createdBy);
            return cb.or(cb.like(join.get(UserEntity_.USER_NAME),"%" + createdBy + "%"), cb.like(join.get(UserEntity_.FULL_NAME),"%" + createdBy + "%"));
        };
    }

    public static Specification<QuestionEntity> filter(QuestionFilterModel filter) {
        List<Specification<QuestionEntity>> specs = new ArrayList<>();
        if (filter.getCategories() != null)
            if (!filter.getCategories().isEmpty())
                specs.add(orIn(QuestionEntity_.CATEGORY, filter.getCategories().stream().map(c -> (Object) c).collect(Collectors.toList())));
        if (filter.getTitle() != null)
            specs.add(like(QuestionEntity_.TITLE, filter.getTitle()));
        if (filter.getQuestContent() != null)
            specs.add(like(QuestionEntity_.QUEST_CONTENT, filter.getQuestContent()));
        if (filter.getReplyContent() != null)
            specs.add(like(QuestionEntity_.REPLY_CONTENT, filter.getReplyContent()));
        if (filter.getStatus() != null)
            if (!filter.getStatus().isEmpty())
                specs.add(orIn(QuestionEntity_.STATUS, filter.getStatus().stream().map(c -> (Object) c).collect(Collectors.toList())));

        if (filter.getMinCreatedDate() != null && filter.getMaxCreatedDate() != null)
            specs.add(betweenDate(QuestionEntity_.CREATED_DATE, filter.getMinCreatedDate(), filter.getMaxCreatedDate()));
        else if (filter.getMinCreatedDate() != null)
            specs.add(dateGreaterThanEqual(QuestionEntity_.CREATED_DATE, filter.getMinCreatedDate()));
        else if (filter.getMaxUpdatedDate() != null)
            specs.add(dateLessThanEqual(QuestionEntity_.CREATED_DATE, filter.getMaxCreatedDate()));

        if (filter.getMinUpdatedDate() != null && filter.getMaxUpdatedDate() != null)
            specs.add(betweenDate(QuestionEntity_.UPDATED_DATE, filter.getMinUpdatedDate(), filter.getMaxUpdatedDate()));
        else if (filter.getMinUpdatedDate() != null)
            specs.add(dateGreaterThanEqual(QuestionEntity_.UPDATED_DATE, filter.getMinUpdatedDate()));
        else if (filter.getMaxUpdatedDate() != null)
            specs.add(dateLessThanEqual(QuestionEntity_.UPDATED_DATE, filter.getMaxUpdatedDate()));

        if (filter.getCreatedBy() != null)
            specs.add(likeCreatedBy(filter.getCreatedBy()));
        Specification finalSpec = null;

        for (Specification<QuestionEntity> spec : specs) {
            if (finalSpec == null)
                finalSpec = spec;
            else
                finalSpec = finalSpec.and(spec);
        }
        return finalSpec;
    }

}
