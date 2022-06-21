package com.models.specifications;

import com.entities.*;
import com.models.filters.*;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ProductSpecification {
    public static Specification<ProductEntity> like(String t) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.or(
                criteriaBuilder.like(root.get(ProductEntity_.NAME), t)
        );
    }

    public static Specification<ProductEntity> byProductName(String pName) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(ProductEntity_.NAME), pName);
    }

    public static Specification<ProductEntity> byActive(boolean active){
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(ProductEntity_.ACTIVE), active);
    }


    public static Specification<ProductEntity> byProductActive(Boolean pActive) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(ProductEntity_.ACTIVE), pActive);
    }
    public static Specification<ProductEntity> byDate(DateFilterModel date) {
        return ((root, query, criteriaBuilder) -> {
            if(date.getMaxDate()!=null){
                Instant instant = date.getMaxDate().toInstant();
                instant = instant.plus(1, ChronoUnit.DAYS);
                Instant finalInstant = instant;
                if(date.getMinDate()!=null)
                    return criteriaBuilder.between(root.get(ProductEntity_.CREATED_DATE),date.getMinDate(),Date.from(finalInstant));
                else
                return criteriaBuilder.lessThanOrEqualTo(root.get(ProductEntity_.CREATED_DATE),Date.from(finalInstant));
            }else if(date.getMinDate()!=null){
                return criteriaBuilder.greaterThanOrEqualTo(root.get(ProductEntity_.CREATED_DATE),date.getMinDate());
            }else {
                return null;
            }
        });
    }
    public static Specification<ProductEntity> byLike(LikeFilterModel like) {
        return ((root, query, criteriaBuilder) -> {
            if(like.getMinLike()!=null && like.getMaxLike()!=null){
                return criteriaBuilder.between(root.get(ProductEntity_.TOTAL_LIKE),like.getMinLike(),like.getMaxLike());

            }
            else if(like.getMaxLike()!=null){
                return criteriaBuilder.lessThanOrEqualTo(root.get(ProductEntity_.TOTAL_LIKE),like.getMaxLike());
            }else if(like.getMinLike()!=null){
                return criteriaBuilder.greaterThanOrEqualTo(root.get(ProductEntity_.TOTAL_LIKE),like.getMinLike());
            }else {
                return null;
            }
        });
    }
    public static Specification<ProductEntity> byReview(ReviewFilterModel rv) {
        return ((root, query, criteriaBuilder) -> {
            if(rv.getMinReview()!=null && rv.getMaxReview()!=null){
                return criteriaBuilder.between(root.get(ProductEntity_.TOTAL_REVIEW),rv.getMinReview(),rv.getMaxReview());

            }
            else if(rv.getMaxReview()!=null){
                return criteriaBuilder.lessThanOrEqualTo(root.get(ProductEntity_.TOTAL_REVIEW),rv.getMaxReview());
            }else if(rv.getMinReview()!=null){
                return criteriaBuilder.greaterThanOrEqualTo(root.get(ProductEntity_.TOTAL_REVIEW),rv.getMinReview());
            }else {
                return null;
            }
        });
    }

    public static Specification<ProductEntity> byRating(RatingFilterModel rate) {
        return ((root, query, criteriaBuilder) -> {
            if(rate.getMinRating()!=null && rate.getMaxRating()!=null){
                return criteriaBuilder.between(root.get(ProductEntity_.CREATED_DATE),rate.getMinRating(),rate.getMaxRating());

            }
            else if(rate.getMaxRating()!=null){
                return criteriaBuilder.lessThanOrEqualTo(root.get(ProductEntity_.CREATED_DATE),rate.getMaxRating());
            }else if(rate.getMinRating()!=null){
                return criteriaBuilder.greaterThanOrEqualTo(root.get(ProductEntity_.CREATED_DATE),rate.getMinRating());
            }else {
                return null;
            }
        });
    }
    public static Specification<ProductEntity> byMaxRating(int maxRating) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get(ProductEntity_.RATING), maxRating);
    }

    public static Specification<ProductEntity> byMeta(ProductMetaFilterModel metaModel) {
        return ((root, query, criteriaBuilder) -> {
            Join<ProductEntity, ProductMetaEntity> metaEntityRoot = root.join(ProductEntity_.PRODUCT_METAS);
            return metaEntityRoot.on(criteriaBuilder.equal(metaEntityRoot.get(ProductMetaEntity_.META_KEY), metaModel.getKey()))
                    .on(criteriaBuilder.equal(metaEntityRoot.get(ProductMetaEntity_.META_VALUE), metaModel.getValue()))
                    .getOn();
        });
    }

    public static Specification<ProductEntity> byMetaKey(String metaKey) {
        return ((root, query, criteriaBuilder) -> {
            Join<ProductEntity, ProductMetaEntity> metaEntityRoot = root.join(ProductEntity_.PRODUCT_METAS);
            return metaEntityRoot.on(criteriaBuilder.equal(metaEntityRoot.get(ProductMetaEntity_.META_KEY), metaKey))
                    .getOn();
        });
    }

    public static Specification<ProductEntity> byCategory(List<String> categorySlug) {
        return ((root, query, criteriaBuilder) -> {
            Join<ProductEntity, CategoryEntity> categoryEntityRoot = root.join(ProductEntity_.CATEGORY, JoinType.INNER);
            List<Predicate> listPre = new ArrayList<>();
            for (String c: categorySlug){
                Predicate internalPre = criteriaBuilder.or(criteriaBuilder.equal(categoryEntityRoot.get(CategoryEntity_.SLUG),c));
                listPre.add(internalPre);
            }
                return criteriaBuilder.or(listPre.toArray(new Predicate[0]));
        });
    }

    public static Specification<ProductEntity> byPrice(PriceFilterModel price) {
        return ((root, query, criteriaBuilder) -> {
            Join<ProductEntity, OptionEntity> metaEntityRoot = root.join(ProductEntity_.OPTIONS);
            if(price.getMinPrice()!=null && price.getMaxPrice()!=null){
                return metaEntityRoot.on(criteriaBuilder.between(metaEntityRoot.get(OptionEntity_.NEW_PRICE),price.getMinPrice(),price.getMaxPrice()))
                        .getOn();
            }
            else if(price.getMaxPrice()!=null){
                return metaEntityRoot.on(criteriaBuilder.lessThanOrEqualTo(metaEntityRoot.get(OptionEntity_.NEW_PRICE),price.getMaxPrice()))
                        .getOn();
            }else if(price.getMinPrice()!=null){
                return metaEntityRoot.on(criteriaBuilder.greaterThanOrEqualTo(metaEntityRoot.get(OptionEntity_.NEW_PRICE),price.getMinPrice()))
                        .getOn();
            }else {
                return null;
            }
        });
    }
//    public static Specification<ProductEntity> byMaxPrice(Double maxPrice) {
//        return ((root, query, criteriaBuilder) -> {
//            Join<ProductEntity, OptionEntity> metaEntityRoot = root.join(OptionEntity_.PRODUCT_ID);
//            return metaEntityRoot.on(criteriaBuilder.lessThanOrEqualTo(metaEntityRoot.get(OptionEntity_.NEW_PRICE),maxPrice))
//                    .getOn();
//        });
//    }





//    public static Specification<ProductEntity> id_in(List<Long> ids){
//        return (root, query, criteriaBuilder) -> root.get(ProductEntity_.ID).in(ids);
//    }
//    public static Specification<ProductEntity> id_not_in(List<Long> ids){
//        return (root, query, criteriaBuilder) -> root.get(ProductEntity_.ID).in(ids).not();
//    }

    public static Specification<ProductEntity> filter(ProductFilter filter) {
        List<Specification<ProductEntity>> specifications = new ArrayList<>();

        if (filter.getT() != null)
            specifications.add(like(filter.getT()));
        if (filter.getName() != null)
            specifications.add(byProductName(filter.getName()));

        if (filter.getDate() != null)
            specifications.add(byDate(filter.getDate()));
        if (filter.getPrice() != null)
            specifications.add(byPrice(filter.getPrice()));
        if (filter.getLikeFilterModel() != null)
            specifications.add(byLike(filter.getLikeFilterModel()));
        if (filter.getReviewFilterModel() != null)
            specifications.add(byReview(filter.getReviewFilterModel()));
        if (filter.getRatingFilterModel() != null)
            specifications.add(byRating(filter.getRatingFilterModel()));
        if (filter.getMetaKey() != null)
            specifications.add(byMetaKey(filter.getMetaKey()));
        if (filter.getMetas() != null)
            filter.getMetas().forEach(meta -> specifications.add(byMeta(meta)));


        Specification<ProductEntity> finalSpec = null;
        for (Specification<ProductEntity> s : specifications
        ) {
            if (finalSpec == null) {
                finalSpec = s;
            } else
                finalSpec = finalSpec.and(s);
        }
        if (filter.getCategorySlugs() != null){
            if(finalSpec == null) {
                finalSpec = byCategory(filter.getCategorySlugs());
            } else {
                finalSpec.or(byCategory(filter.getCategorySlugs()));
            }
        }



        return finalSpec;
    }
}
