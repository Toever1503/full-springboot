package com.repositories;

import com.entities.CategoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ICategoryRepository extends JpaRepository<CategoryEntity, Long> {
    Page<CategoryEntity> findAllByParentCategoryId(Long id, Pageable pageable);
    Optional<CategoryEntity> findBySlug(String slug);
}
