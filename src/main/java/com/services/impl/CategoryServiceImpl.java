package com.services.impl;

import com.entities.CategoryEntity;
import com.models.CategoryModel;
import com.repositories.ICategoryRepository;
import com.services.ICategoryService;
import com.utils.ASCIIConverter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements ICategoryService {
    private final ICategoryRepository categoryRepository;

    public CategoryServiceImpl(ICategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<CategoryEntity> findAll() {
        return null;
    }

    @Override
    public Page<CategoryEntity> findAll(Pageable page) {
        return this.categoryRepository.findAll(page);
    }

    @Override
    public Page<CategoryEntity> findChildrenById(Long id, Pageable pageable) {
        return this.categoryRepository.findAllByParentCategoryId(id, pageable);
    }

    @Override
    public CategoryEntity findBySlug(String slug) {
        return this.categoryRepository.findBySlug(slug).orElseThrow(() -> new RuntimeException("Category not found" + slug));
    }


    @Override
    public Page<CategoryEntity> filter(Pageable page, Specification<CategoryEntity> specs) {
        return null;
    }

    @Override
    public CategoryEntity findById(Long id) {
        return this.categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Category not found: " + id));
    }

    @Override
    public CategoryEntity add(CategoryModel model) {
        CategoryEntity categoryEntity = CategoryModel.toEntity(model);
        if(model.getParentId() != null) {
            CategoryEntity parent = this.findById(model.getParentId());
            categoryEntity.setParentCategory(parent);
        }
        return this.categoryRepository.save(categoryEntity);
    }

    @Override
    public List<CategoryEntity> add(List<CategoryModel> model) {
        return null;
    }

    @Override
    public CategoryEntity update(CategoryModel model) {
        CategoryEntity originCategory = this.categoryRepository.findById(model.getId()).get();
        if(this.categoryRepository.findById(model.getId()).isPresent()) {
            originCategory.setType(model.getType().name());
            originCategory.setCategoryName(model.getCategoryName());
            originCategory.setSlug(model.getSlug() == null ? ASCIIConverter.utf8ToAscii(model.getCategoryName()) : ASCIIConverter.utf8ToAscii(model.getSlug()));
            originCategory.setDescription(model.getDescription());
            if(model.getParentId() != null) {
                originCategory.setParentCategory(this.categoryRepository.findById(model.getParentId()).get());
            }
            return this.categoryRepository.save(originCategory);
        }
        return null;
    }

    @Override
    public boolean deleteById(Long id) {
        this.categoryRepository.deleteById(id);
        return true;
    }

    @Override
    public boolean deleteByIds(List<Long> ids) {
        return false;
    }

}
