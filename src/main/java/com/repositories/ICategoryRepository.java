package com.repositories;

import com.entities.CategoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ICategoryRepository extends JpaRepository<CategoryEntity, Long> {
    List<CategoryEntity> findAllByParentCategoryId(Long id);

    Optional<CategoryEntity> findBySlug(String slug);

    @Modifying
    @Query(value = "update tbl_category set parent_id = null where parent_id = ?1", nativeQuery = true)
    void updateCategoryParent(Long catId);

    @Query("SELECT c FROM CategoryEntity c WHERE c.slug LIKE %?1% or c.categoryName LIKE %?1% or c.description LIKE %?1%")
    Page<CategoryEntity> search(String q, Pageable page);
}
