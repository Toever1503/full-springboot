package com.services.impl;

import com.entities.CategoryEntity;
import com.entities.ProductEntity;
import com.models.CategoryModel;
import com.repositories.ICategoryRepository;
import com.repositories.IProductRepository;
import com.services.ICategoryService;
import com.utils.ASCIIConverter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements ICategoryService {
    private final ICategoryRepository categoryRepository;
    private final IProductRepository productRepository;

    public CategoryServiceImpl(ICategoryRepository categoryRepository, IProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @Override
    public List<CategoryEntity> findAll() {
        return this.categoryRepository.findAlLS();
    }

    @Override
    public Page<CategoryEntity> findAll(Pageable page) {
        return this.categoryRepository.findAll(page);
    }

    @Override
    public List<CategoryEntity> findChildrenById(Long id) {
        return this.categoryRepository.findAllByParentCategoryId(id);
    }

    @Override
    public CategoryEntity findBySlug(String slug) {
        return this.categoryRepository.findBySlug(slug).orElseThrow(() -> new RuntimeException("Category not found" + slug));
    }

    @Override
    public Page<CategoryEntity> search(String q, Pageable page) {
        return this.categoryRepository.search(q, page);
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
        categoryEntity.setDeepLevel(0);
        if (this.categoryRepository.findBySlug(model.getSlug()).isPresent())
            throw new RuntimeException("Slug already existed!");

        this.saveParentCategory(categoryEntity, model.getParentId());
        return this.categoryRepository.save(categoryEntity);
    }

    @Override
    public List<CategoryEntity> add(List<CategoryModel> model) {
        return null;
    }

    @Override
    public CategoryEntity update(CategoryModel model) {
        String slug = model.getSlug() == null ? ASCIIConverter.utf8ToAscii(model.getCategoryName()) : ASCIIConverter.utf8ToAscii(model.getSlug());
        CategoryEntity checkedCategory = this.categoryRepository.findBySlug(slug).orElse(null);
        if (checkedCategory != null)
            if (!checkedCategory.getId().equals(model.getId()))
                throw new RuntimeException("Slug already existed!");

        CategoryEntity originCategory = this.findById(model.getId());
        originCategory.setCategoryName(model.getCategoryName());
        originCategory.setSlug(slug);
        originCategory.setDescription(model.getDescription());
        this.saveParentCategory(originCategory, model.getParentId());
        return this.categoryRepository.save(originCategory);
    }

    private void saveParentCategory(CategoryEntity entity, Long parentId) {
        if (parentId != null) { //check if parent id not null\
            CategoryEntity parentCategory = this.findById(parentId);
            if (parentCategory.getDeepLevel() >= 3)
                throw new RuntimeException("Parent category is too deep!");
            entity.setParentCategory(parentCategory);
            entity.setDeepLevel(parentCategory.getDeepLevel() + 1);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        this.categoryRepository.deleteById(id);
        this.categoryRepository.updateCategoryParent(id);
        return true;
    }

    @Override
    public boolean deleteByIds(List<Long> ids) {
        return false;
    }

}
