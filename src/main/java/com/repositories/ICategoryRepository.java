package com.repositories;

import com.entities.CategoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ICategoryRepository extends JpaRepository<CategoryEntity, Long>, JpaSpecificationExecutor<CategoryEntity> {
    List<CategoryEntity> findAllByParentCategoryIdAndType(Long id, String type);

    Optional<CategoryEntity> findBySlug(String slug);

    @Query("select c.industry.slug from CategoryEntity c where c.slug = ?1")
    Optional<String> findByCategorySlug(String slug);

    @Query("SELECT c FROM CategoryEntity c ORDER BY c.id DESC")
    List<CategoryEntity> findAlLS();

    @Modifying
    @Query(value = "update tbl_category set parent_id = null where parent_id = ?1 and type = 'CATEGORY'", nativeQuery = true)
    void updateCategoryParent(Long catId);


    @Query("SELECT c FROM CategoryEntity c WHERE c.slug LIKE %?1% or c.categoryName LIKE %?1% or c.description LIKE %?1%")
    Page<CategoryEntity> search(String q, Pageable page);

    @Modifying
    @Query("UPDATE ProductEntity p SET p.category = null WHERE p.category.id = ?1")
    void updateProductCategory(Long categoryId);

    @Modifying
    void deleteByIdAndType(Long id, String type);
}
