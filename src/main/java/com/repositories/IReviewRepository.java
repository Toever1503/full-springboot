package com.repositories;

import com.entities.ReviewEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IReviewRepository extends JpaRepository<ReviewEntity, Long> {
    @Modifying
    @Query(value = "DELETE FROM tbl_review WHERE parent_id = ?1", nativeQuery = true)
    void deleteReviewByParent(Long catId);

    @Modifying
    @Query(value = "update tbl_product as p\n" +
            "set rating = (select sum(rv.rating)/count(rv.id) from tbl_review as rv where rv.product_id=?1)\n" +
            "where p.id=?1", nativeQuery = true)
    void updateProductRating(Long productId);

    Page<ReviewEntity> findAllByParentReviewIsNull(Pageable page);

    Page<ReviewEntity> findAllByParentReviewIsNullAndStatusAndProductId(Pageable page, String status, Long productId);
}
