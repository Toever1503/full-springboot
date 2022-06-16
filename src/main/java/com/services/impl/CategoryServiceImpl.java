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
        return this.categoryRepository.findAll();
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
        if(model.getParentId() != null) { //check if parent id not null
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
        if(this.categoryRepository.findById(model.getId()).isPresent()) { //check if category exist
            originCategory.setCategoryName(model.getCategoryName());
            originCategory.setSlug(model.getSlug() == null ? ASCIIConverter.utf8ToAscii(model.getCategoryName()) : ASCIIConverter.utf8ToAscii(model.getSlug()));
            originCategory.setDescription(model.getDescription());
            if(model.getParentId() != null) {//check if parent id not null
                originCategory.setParentCategory(this.categoryRepository.findById(model.getParentId()).get());
            }
            return this.categoryRepository.save(originCategory);
        }
        return this.categoryRepository.save(originCategory);
    }

    @Override
    public boolean deleteById(Long id) {
        CategoryEntity categoryEntity = this.findById(id);
        categoryEntity.getProducts().forEach(product -> {
            ProductEntity productEntity = this.productRepository.findById(product.getId()).orElse(null);
            if(productEntity != null) {
                productEntity.setCategory(null);
                this.productRepository.save(productEntity);
            }
        });
        this.categoryRepository.deleteById(id);
        return true;
    }

    @Override
    public boolean deleteByIds(List<Long> ids) {
        return false;
    }

}
