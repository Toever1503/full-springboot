package com.repositories;

import com.entities.UserLikeProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface IUserLikeProductRepository extends JpaRepository<UserLikeProductEntity, Long> {
    UserLikeProductEntity findFirstByProductIdAndUserId(Long pid, Long uid);

    @Query("select ulp from  UserLikeProductEntity ulp where ulp.userId = ?2 and ulp.productId = ?1")
    Optional<UserLikeProductEntity> getIsLikedProduct(Long pId, Long userId);
}
