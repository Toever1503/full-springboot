package com.repositories;

import com.entities.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IReviewRepository extends JpaRepository<ReviewEntity, Long> {
    @Modifying
    @Query(value = "update tbl_review set parent_id = null where parent_id = ?1", nativeQuery = true)
    void updateReviewParent(Long catId);
}
