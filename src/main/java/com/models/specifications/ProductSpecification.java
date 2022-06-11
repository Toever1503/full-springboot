package com.models.specifications;

import com.entities.ProductEntity;
import com.entities.ProductEntity_;
import com.entities.ProductMetaEntity;
import com.entities.ProductMetaEntity_;
import com.models.filters.ProductFilter;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import java.util.ArrayList;
import java.util.List;

public class ProductSpecification {
    public static Specification<ProductEntity> like(String t) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.or(
                criteriaBuilder.like(root.get(ProductEntity_.NAME), t),
                criteriaBuilder.like(root.get(ProductEntity_.SLUG), t)
        );
    }

    public static Specification<ProductEntity> byProductName(String pName) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(ProductEntity_.NAME), pName);
    }

    public static Specification<ProductEntity> byProductSlug(String pSlug) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(ProductEntity_.SLUG), pSlug);
    }

    public static Specification<ProductEntity> byProductActive(Boolean pActive) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(ProductEntity_.ACTIVE), pActive);
    }

    public static Specification<ProductEntity> byMeta(ProductMetaFilterModel metaModel) {
        return ((root, query, criteriaBuilder) -> {
            Join<ProductEntity, ProductMetaEntity> metaEntityRoot = root.join(ProductMetaEntity_.PRODUCT_ID);
            return metaEntityRoot.on(criteriaBuilder.equal(metaEntityRoot.get(ProductMetaEntity_.META_KEY), metaModel.getKey()))
                    .on(criteriaBuilder.equal(metaEntityRoot.get(ProductMetaEntity_.META_VALUE), metaModel.getValue()))
                    .getOn();
        });
    }

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
        if (filter.getActive() != null)
            specifications.add(byProductActive(filter.getActive()));
        if (filter.getName() != null)
            specifications.add(byProductName(filter.getName()));
        if (filter.getSlug() != null)
            specifications.add(byProductSlug(filter.getSlug()));
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
        return finalSpec;
    }
}
