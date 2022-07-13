package com.services.impl;

import com.dtos.ECategoryType;
import com.dtos.ProductMetaDto2;
import com.dtos.ProductVariationDto2;
import com.entities.CategoryEntity;
import com.entities.UserEntity;
import com.models.CategoryModel;
import com.models.specifications.CategorySpecification;
import com.repositories.ICategoryRepository;
import com.repositories.IProductMetaRepository;
import com.repositories.IProductVariationRepository;
import com.services.ICategoryService;
import com.utils.ASCIIConverter;
import com.utils.FileUploadProvider;
import com.utils.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements ICategoryService {
    private final ICategoryRepository categoryRepository;


    final FileUploadProvider fileUploadProvider;

    private final IProductVariationRepository productVariationRepository;

    private final IProductMetaRepository productMetaRepository;

    private final Executor taskExecutor;

    public CategoryServiceImpl(ICategoryRepository categoryRepository,
                               FileUploadProvider fileUploadProvider, IProductVariationRepository productVariationRepository,
                               IProductMetaRepository productMetaRepository,
                               Executor taskExecutor) {
        this.categoryRepository = categoryRepository;
        this.fileUploadProvider = fileUploadProvider;
        this.productVariationRepository = productVariationRepository;
        this.productMetaRepository = productMetaRepository;
        this.taskExecutor = taskExecutor;
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
    public List<CategoryEntity> findAll(Specification<CategoryEntity> specs) {
        return this.categoryRepository.findAll(specs);
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
    public CategoryEntity findOne(Specification<CategoryEntity> spec) {
        return this.categoryRepository.findOne(spec).orElseThrow(() -> new RuntimeException("Category not found"));
    }

    @Transactional(propagation = Propagation.NESTED)
    protected CompletableFuture<List<ProductMetaDto2>> findProductMetasFuture(Long industryId) {
        return CompletableFuture.supplyAsync(() ->
                this.productMetaRepository.findAllMetaKeysByProductCategoryId(industryId)
                        .stream().map(metaKey -> ProductMetaDto2.builder()
                                .metaKey(metaKey)
                                .metaValues(this.productMetaRepository.findAllMetaValuesByMetaKey(metaKey))
                                .build())
                        .collect(Collectors.toList()), this.taskExecutor);
    }

    @Transactional(propagation = Propagation.NESTED)
    protected CompletableFuture<List<ProductVariationDto2>> findProductVariationsFuture(Long categoryId) {
        return CompletableFuture.supplyAsync(() ->
                this.productVariationRepository.findAllVariationNamesByProduct(categoryId)
                        .stream().map(variationName ->
                                ProductVariationDto2.builder()
                                        .variationName(variationName)
                                        .variationValues(this.productVariationRepository.findALlVariationValuesByVariationName(variationName))
                                        .build())
                        .collect(Collectors.toList()), this.taskExecutor);
    }


    @Override
    public boolean changeStatus(Long id) {
        CategoryEntity entity = this.findById(id);
        entity.setStatus(!entity.getStatus());
        return this.categoryRepository.save(entity) != null;
    }


    @Override
    public Page<CategoryEntity> filter(Pageable page, Specification<CategoryEntity> specs) {
        return null;
    }

    @Override
    public Page<CategoryEntity> filterByStatus(Pageable page, Boolean status) {
        return categoryRepository.findAll(CategorySpecification.byStatus(status), page);
    }

    @Override
    public List<CategoryEntity> getAllCategories() {
        return this.categoryRepository.findAllPublicAndStatus(true);
    }

    @Override
    public CategoryEntity findById(Long id) {
        return this.categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Category not found: " + id));
    }

    @Override
    public CategoryEntity add(CategoryModel model) {
        CategoryEntity categoryEntity = CategoryModel.toEntity(model);


        final String folder = UserEntity.FOLDER + SecurityUtils.getCurrentUsername() + "/" + CategoryEntity.FOLDER;
        // save file
        if (model.getImage() != null) {//Check if notification avatar is empty or not
            String filePath;
            try {
                filePath = fileUploadProvider.uploadFile(folder, model.getImage());
                categoryEntity.setCatFile(filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        categoryEntity.setCreatedBy(SecurityUtils.getCurrentUser().getUser());
        categoryEntity.setTotalProduct(0);
        categoryEntity.setDeepLevel(0);
        categoryEntity.setStatus(false);
        if (this.categoryRepository.findBySlug(model.getSlug()).isPresent())
            throw new RuntimeException("Slug already existed!");

        this.saveParentCategory(categoryEntity, model.getParentId());
        categoryEntity = this.categoryRepository.saveAndFlush(categoryEntity);

        return categoryEntity;
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
        final String folder = UserEntity.FOLDER + originCategory.getCreatedBy().getUserName() + "/" + CategoryEntity.FOLDER;
        originCategory.setCreatedBy(SecurityUtils.getCurrentUser().getUser());
        originCategory.setCategoryName(model.getCategoryName());
        originCategory.setSlug(slug);
        originCategory.setDescription(model.getDescription());
        this.saveParentCategory(originCategory, model.getParentId());
        // delete old file and save new file
        if (model.getImage().getOriginalFilename() != null && !model.getImage().getOriginalFilename().equals("")) {
            String filePath;
            try {
                filePath = fileUploadProvider.uploadFile(folder, model.getImage());
                fileUploadProvider.deleteFile(originCategory.getCatFile());
                originCategory.setCatFile(filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        originCategory = this.categoryRepository.saveAndFlush(originCategory);
        return originCategory;
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
        if (id.equals(1l))
            return false;
        this.categoryRepository.updateCategoryParent(id);
        this.categoryRepository.updateProductCategory(id);
        this.categoryRepository.deleteById(id);
        return true;
    }

    @Override
    public boolean deleteByIds(List<Long> ids) {
        return false;
    }

}
