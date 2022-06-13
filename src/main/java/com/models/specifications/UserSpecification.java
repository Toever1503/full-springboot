package com.models.specifications;

import com.entities.UserEntity;
import com.entities.UserEntity_;
import com.models.filters.UserFilterModel;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class UserSpecification extends BaseSpecification {


    public static Specification<UserEntity> filter(UserFilterModel filter) {
        List<Specification<UserEntity>> specs = new ArrayList<>();
        if (filter.getUsername() != null)
            specs.add(like(filter.getUsername(), UserEntity_.USER_NAME));
        if (filter.getFullname() != null)
            specs.add(like(filter.getFullname(), UserEntity_.FULL_NAME));
        if (filter.getEmail() != null)
            specs.add(like(filter.getEmail(), UserEntity_.EMAIL));
        if (filter.getPhone() != null)
            specs.add(like(filter.getPhone(), UserEntity_.PHONE));
        if (filter.getSex() != null)
            specs.add(like(filter.getSex(), UserEntity_.SEX));
        if (filter.getMaxBirthDay() != null && filter.getMinBirthDay() != null)
            specs.add(betweenDate(UserEntity_.BIRTH_DATE, filter.getMinBirthDay(), filter.getMaxBirthDay()));
        if (filter.getMaxCreatedDate() != null && filter.getMinCreatedDate() != null)
            specs.add(betweenDate(UserEntity_.CREATED_DATE, filter.getMinCreatedDate(), filter.getMaxCreatedDate()));
        if (filter.getMaxUpdatedDate() != null && filter.getMinUpdatedDate() != null)
            specs.add(betweenDate(UserEntity_.UPDATED_DATE, filter.getMinUpdatedDate(), filter.getMaxUpdatedDate()));
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
