package com.repositories;

import com.entities.UserLikeProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUserLikeProductRepository extends JpaRepository<UserLikeProductEntity, Long> {
    UserLikeProductEntity findFirstByProductIdAndUserId(Long pid, Long uid);
}
