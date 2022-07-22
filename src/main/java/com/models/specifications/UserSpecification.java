package com.models.specifications;

import com.entities.OrderEntity_;
import com.entities.UserEntity;
import com.entities.UserEntity_;
import com.models.filters.UserFilterModel;
import org.springframework.data.jpa.domain.Specification;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserSpecification extends BaseSpecification {


    public static Specification<UserEntity> filter(UserFilterModel filter) throws ParseException {
        List<Specification<UserEntity>> specs = new ArrayList<>();
        if (filter.getUsername() != null)
            specs.add(like(UserEntity_.USER_NAME, filter.getUsername()));
        if (filter.getFullname() != null)
            specs.add(like(UserEntity_.FULL_NAME, filter.getFullname()));
        if (filter.getEmail() != null)
            specs.add(like(UserEntity_.EMAIL, filter.getEmail()));
        if (filter.getPhone() != null)
            specs.add(like(UserEntity_.PHONE, filter.getPhone()));
        if (filter.getSex() != null)
            specs.add(orIn(UserEntity_.SEX, filter.getSex().stream().map(e -> (Object) e).collect(Collectors.toList())));
        if (filter.getStatus() != null)
            specs.add(equal(UserEntity_.STATUS, filter.getStatus()));
        if (filter.getLockStatus() != null)
            specs.add(equal(UserEntity_.LOCK_STATUS, filter.getLockStatus()));
        if (filter.getMaxBirthDay() != null && filter.getMinBirthDay() != null)
            specs.add(betweenDate(UserEntity_.BIRTH_DATE, filter.getMinBirthDay(), filter.getMaxBirthDay()));

        if (filter.getMinCreatedDate() != null && filter.getMaxCreatedDate() != null) {
            specs.add(betweenDate(UserEntity_.CREATED_DATE, filter.getMinCreatedDate(), filter.getMaxCreatedDate()));
        } else if (filter.getMinCreatedDate() != null) {
            specs.add(dateGreaterThanEqual(UserEntity_.CREATED_DATE, filter.getMinCreatedDate()));
        } else if (filter.getMaxCreatedDate() != null) {
            specs.add(dateLessThanEqual(UserEntity_.CREATED_DATE, filter.getMaxCreatedDate()));
        }

        if (filter.getMinUpdatedDate() != null && filter.getMaxUpdatedDate() != null) {
            specs.add(betweenDate(UserEntity_.UPDATED_DATE, filter.getMinUpdatedDate(), filter.getMaxUpdatedDate()));
        } else if (filter.getMinUpdatedDate() != null) {
            specs.add(dateGreaterThanEqual(UserEntity_.UPDATED_DATE, filter.getMinUpdatedDate()));
        } else if (filter.getMaxUpdatedDate() != null) {
            specs.add(dateLessThanEqual(UserEntity_.UPDATED_DATE, filter.getMaxUpdatedDate()));
        }


        Specification<UserEntity> finalSpec = null;
        for (Specification<UserEntity> spec : specs) {
            if (finalSpec == null)
                finalSpec = spec;
            else
                finalSpec = finalSpec.and(spec);
        }
        return finalSpec;
    }
}
