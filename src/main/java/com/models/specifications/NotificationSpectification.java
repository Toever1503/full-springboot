package com.models.specifications;

import com.entities.*;
import com.models.filters.NotificationFilter;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NotificationSpectification {
    public static Specification<NotificationEntity> like(String keyword) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.or(
                criteriaBuilder.like(root.get(NotificationEntity_.TITLE), keyword),
                criteriaBuilder.like(root.get(NotificationEntity_.CONTENT), keyword),
                criteriaBuilder.like(root.get(NotificationEntity_.CONTENT_EXCERPT), keyword)
        );
    }

    public static Specification<NotificationEntity> byNotificationDate(Date fromCreateDate, Date toCreateDate ) {
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
    public static Specification<NotificationEntity> byNotificationTitle(String nTitle) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(NotificationEntity_.TITLE), nTitle);
    }

    public static Specification<NotificationEntity> byNotificationContent(String nContent) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(NotificationEntity_.CONTENT), nContent);
    }

    public static Specification<NotificationEntity> byNotificationContentExcerpt(String nContentExcerpt) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(NotificationEntity_.CONTENT_EXCERPT), nContentExcerpt);
    }

    public static Specification<NotificationEntity> byNotificationStatus(String nStatus) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(NotificationEntity_.STATUS), nStatus);
    }

    public static Specification<NotificationEntity> byMinNotificationViewed(Integer minViewed) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get(NotificationEntity_.VIEWED), minViewed);
    }

    public static Specification<NotificationEntity> byMaxNotificationViewed(Integer maxViewed) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get(NotificationEntity_.VIEWED), maxViewed);
    }

    public static Specification<NotificationEntity> byNotificationCategory(List<String> nCategory) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.or(
                nCategory.stream().map(category -> criteriaBuilder.equal(root.get(NotificationEntity_.CATEGORY), category)).toArray(Predicate[]::new)
        );
    }

    public static Specification<NotificationEntity> filter(NotificationFilter notificationFilter){
        List<Specification<NotificationEntity>> specs =new ArrayList<>();

        if(notificationFilter.getKeyword() != null && !notificationFilter.getKeyword().isEmpty()){
            specs.add(like("%" + notificationFilter.getKeyword() + "%"));
        }
        if(notificationFilter.getTitle()!=null){
            specs.add(byNotificationTitle(notificationFilter.getTitle()));
        }
        if(notificationFilter.getContent()!=null){
            specs.add(byNotificationContent(notificationFilter.getContent()));
        }
        if(notificationFilter.getContentExcerpt()!=null){
            specs.add(byNotificationContentExcerpt(notificationFilter.getContentExcerpt()));
        }
        if(notificationFilter.getStatus()!=null){
            specs.add(byNotificationStatus(notificationFilter.getStatus()));
        }
        if(notificationFilter.getMinViewed()!=null){
            specs.add(byMinNotificationViewed(notificationFilter.getMinViewed()));
        }
        if(notificationFilter.getMaxViewed()!=null){
            specs.add(byMaxNotificationViewed(notificationFilter.getMaxViewed()));
        }
        if(notificationFilter.getCategory()!=null){
            specs.add(byNotificationCategory(notificationFilter.getCategory()));
        }
        if(notificationFilter.getFromCreatedDate()!=null && notificationFilter.getToCreatedDate()!=null){
            specs.add(byNotificationDate(notificationFilter.getFromCreatedDate(), notificationFilter.getToCreatedDate()));
        }

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
