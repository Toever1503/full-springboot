package com.models.specifications;

import com.entities.*;
import com.models.filters.NotificationFilter;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class NotificationSpecification extends BaseSpecification {

    public static Specification<NotificationEntity> byNotificationDate(Date fromCreateDate, Date toCreateDate) {
        return (root, query, criteriaBuilder) -> {
            if (fromCreateDate != null && toCreateDate != null) {
                return criteriaBuilder.between(root.get(NotificationEntity_.CREATED_DATE), fromCreateDate, toCreateDate);
            } else if (fromCreateDate != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get(NotificationEntity_.CREATED_DATE), fromCreateDate);
            } else if (toCreateDate != null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get(NotificationEntity_.CREATED_DATE), toCreateDate);
            } else {
                return null;
            }
        };
    }


    public static Specification<NotificationEntity> byMinNotificationViewed(Integer minViewed) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get(NotificationEntity_.VIEWED), minViewed);
    }

    public static Specification<NotificationEntity> byMaxNotificationViewed(Integer maxViewed) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get(NotificationEntity_.VIEWED), maxViewed);
    }

    public static Specification likeCreatedBy(String createdBy) {
        return (root, query, cb) -> {
            Join<NotificationEntity, UserEntity> join = root.join(NotificationEntity_.createdBy);
            return cb.or(cb.like(join.get(UserEntity_.USER_NAME), createdBy), cb.like(join.get(UserEntity_.FULL_NAME), createdBy));
        };
    }

    public static Specification<NotificationEntity> filter(NotificationFilter notificationFilter) {
        List<Specification<NotificationEntity>> specs = new ArrayList<>();

        if (notificationFilter.getTitle() != null)
            specs.add(like(NotificationEntity_.TITLE, notificationFilter.getTitle()));

        if (notificationFilter.getContent() != null)
            specs.add(like(NotificationEntity_.CONTENT, notificationFilter.getContent()));

        if (notificationFilter.getContentExcerpt() != null)
            specs.add(like(NotificationEntity_.CONTENT_EXCERPT, notificationFilter.getContentExcerpt()));

        if (notificationFilter.getStatus() != null)
            if (!notificationFilter.getStatus().isEmpty())
                specs.add(orIn(NotificationEntity_.STATUS, notificationFilter.getStatus().stream().map(s -> (Object) s).collect(Collectors.toList())));

        if (notificationFilter.getMinViewed() != null)
            specs.add(byMinNotificationViewed(notificationFilter.getMinViewed()));

        if (notificationFilter.getMaxViewed() != null)
            specs.add(byMaxNotificationViewed(notificationFilter.getMaxViewed()));

        if (notificationFilter.getCategory() != null)
            if (!notificationFilter.getCategory().isEmpty())
                specs.add(orIn(NotificationEntity_.CATEGORY, notificationFilter.getCategory().stream().map(s -> (Object) s).collect(Collectors.toList())));

        if (notificationFilter.getFromCreatedDate() != null || notificationFilter.getToCreatedDate() != null)
            specs.add(byNotificationDate(notificationFilter.getFromCreatedDate(), notificationFilter.getToCreatedDate()));


        if (notificationFilter.getCreatedBy() != null)
            specs.add(likeCreatedBy(notificationFilter.getCreatedBy()));


        Specification<NotificationEntity> finalSpec = null;
        for (Specification<NotificationEntity> s : specs) {
            if (finalSpec == null) {
                finalSpec = s;
            } else
                finalSpec = finalSpec.and(s);
        }
        return finalSpec;
    }
}
