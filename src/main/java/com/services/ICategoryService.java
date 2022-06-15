package com.services;

import com.entities.CategoryEntity;
import com.models.CategoryModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.DoubleStream;


public interface ICategoryService extends IBaseService<CategoryEntity, CategoryModel, Long> {
    List<CategoryEntity> findChildrenById(Long id);
    CategoryEntity findBySlug(String slug);

    Page<CategoryEntity> search(String q, Pageable page);
}
