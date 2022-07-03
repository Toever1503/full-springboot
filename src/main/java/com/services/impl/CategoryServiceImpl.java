package com.services.impl;

import com.config.elasticsearch.ERepositories.IEIndustryRepository;
import com.dtos.DetailIndustryDto;
import com.dtos.ECategoryType;
import com.dtos.ProductMetaDto;
import com.dtos.ProductVariationDto;
import com.entities.CategoryEntity;
import com.models.CategoryModel;
import com.models.specifications.CategorySpecification;
import com.repositories.ICategoryRepository;
import com.repositories.IProductMetaRepository;
import com.repositories.IProductVariationRepository;
import com.services.ICategoryService;
import com.utils.ASCIIConverter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements ICategoryService {
    private final ICategoryRepository categoryRepository;

    private final IEIndustryRepository eIndustryRepository;

    private final IProductVariationRepository productVariationRepository;

    private final IProductMetaRepository productMetaRepository;

    private final Executor taskExecutor;

    public CategoryServiceImpl(ICategoryRepository categoryRepository,
                               IEIndustryRepository eIndustryRepository,
                               IProductVariationRepository productVariationRepository,
                               IProductMetaRepository productMetaRepository,
                               Executor taskExecutor) {
        this.categoryRepository = categoryRepository;
        this.eIndustryRepository = eIndustryRepository;
        this.productVariationRepository = productVariationRepository;
        this.productMetaRepository = productMetaRepository;
        this.taskExecutor = taskExecutor;
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
    public List<CategoryEntity> findAll(Specification<CategoryEntity> specs) {
        return this.categoryRepository.findAll(specs);
    }

    @Override
    public List<CategoryEntity> findChildrenById(Long id) {
        return this.categoryRepository.findAllByParentCategoryIdAndType(id, ECategoryType.CATEGORY.name());
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
    public CategoryEntity addIndustry(CategoryModel model) {
        CategoryEntity entity = CategoryModel.toEntity(model);
        entity.setType(ECategoryType.INDUSTRY.name());
        entity.setDeepLevel(0);
        return this.categoryRepository.save(entity);
    }

    @Override
    public CategoryEntity updateIndustry(CategoryModel model) {
        CategoryEntity original = this.findById(model.getId());
        CategoryEntity entity = CategoryModel.toEntity(model);
        entity.setId(original.getId());
        entity.setChildCategories(original.getChildCategories());
        entity.setType(ECategoryType.INDUSTRY.name());
        return this.categoryRepository.save(entity);
    }

    @Override
    public CategoryEntity findOne(Specification<CategoryEntity> spec) {
        return this.categoryRepository.findOne(spec).orElseThrow(() -> new RuntimeException("Category not found"));
    }

    @Override
    public DetailIndustryDto findDetailIndustryByCategorySLug(String slug) {
        return this.findBySLug(this.categoryRepository.findByCategorySlug(slug).orElseThrow(() -> new RuntimeException("Industry category not found: ".concat(slug))));
    }

    private DetailIndustryDto findBySLug(String slug) {
        return this.eIndustryRepository.findBySlug(slug).orElseThrow(() -> new RuntimeException("Industry not found, id: ".concat(slug)));
    }

    @Override
    public DetailIndustryDto findDetailIndustryBySLug(String slug) {
        return this.findBySLug(slug);
    }

    @Override
    public boolean deleteIndustryById(Long id) {
        this.categoryRepository.deleteByIdAndType(id, ECategoryType.INDUSTRY.name());
        this.categoryRepository.updateProductIndustry(id);
        this.categoryRepository.updateCategoryIndustry(id);
        this.eIndustryRepository.deleteById(id);
        return true;
    }


    private DetailIndustryDto syncIndustryOnElasticsearch(CategoryEntity entity) {
        DetailIndustryDto dto = DetailIndustryDto.toDto(entity);
        CompletableFuture all = CompletableFuture.allOf(
                this.findProductMetasFuture(entity.getId()).thenAccept(dto::setProductMetas),
                this.findProductVariationsFuture(entity.getId()).thenAccept(dto::setProductVariations)
        );
        try {
            all.get();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return this.eIndustryRepository.save(dto);
    }

    @Transactional(propagation = Propagation.NESTED)
    protected CompletableFuture<List<DetailIndustryDto.ProductMetaDto2>> findProductMetasFuture(Long industryId) {
        return CompletableFuture.supplyAsync(() ->
                this.productMetaRepository.findAllMetaKeysByProductIndustryId(industryId)
                        .stream().map(metaKey -> DetailIndustryDto.ProductMetaDto2.builder()
                                .metaKey(metaKey)
                                .metaValues(this.productMetaRepository.findAllMetaValuesByMetaKey(metaKey))
                                .build())
                        .collect(Collectors.toList()), this.taskExecutor);
    }

    @Transactional(propagation = Propagation.NESTED)
    protected CompletableFuture<List<DetailIndustryDto.ProductVariationDto2>> findProductVariationsFuture(Long industryId) {
        return CompletableFuture.supplyAsync(() ->
                this.productVariationRepository.findAllVariationNamesByProductIndustryId(industryId)
                        .stream().map(variationName ->
                                DetailIndustryDto.ProductVariationDto2.builder()
                                        .variationName(variationName)
                                        .variationValues(this.productVariationRepository.findALlVariationValuesByVariationName(variationName))
                                        .build())
                        .collect(Collectors.toList()), this.taskExecutor);
    }

    @Override
    public boolean resyncIndustriesOnElasticsearch() {
        List<CategoryEntity> industries = this.categoryRepository.findAll(CategorySpecification.byType(ECategoryType.INDUSTRY));
        industries.forEach(this::syncIndustryOnElasticsearch);
        return true;
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
        categoryEntity.setType(ECategoryType.CATEGORY.name());
        categoryEntity.setDeepLevel(0);
        if (this.categoryRepository.findBySlug(model.getSlug()).isPresent())
            throw new RuntimeException("Slug already existed!");

        this.saveParentCategory(categoryEntity, model.getParentId());
        this.saveIndustry(categoryEntity, model.getIndustryId());
        categoryEntity = this.categoryRepository.save(categoryEntity);

        this.syncIndustryOnElasticsearch(categoryEntity);
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
        originCategory.setCategoryName(model.getCategoryName());
        originCategory.setSlug(slug);
        originCategory.setDescription(model.getDescription());
        this.saveParentCategory(originCategory, model.getParentId());
        this.saveIndustry(originCategory, model.getIndustryId());

        originCategory = this.categoryRepository.save(originCategory);
        this.syncIndustryOnElasticsearch(originCategory);
        return originCategory;
    }

    private void saveIndustry(CategoryEntity entity, Long industry) {
        if (industry != null) { //check if parent id not null\
            CategoryEntity parentCategory = this.findById(industry);
            if (parentCategory.getType().equals(ECategoryType.CATEGORY.name()))
                throw new RuntimeException("Category can't be industry");
            entity.setIndustry(parentCategory);
        }
    }

    private void saveParentCategory(CategoryEntity entity, Long parentId) {
        if (parentId != null) { //check if parent id not null\
            CategoryEntity parentCategory = this.findById(parentId);
            if (parentCategory.getType().equals(ECategoryType.INDUSTRY.name()))
                throw new RuntimeException("Industry can't be parent category");
            if (parentCategory.getDeepLevel() >= 3)
                throw new RuntimeException("Parent category is too deep!");
            entity.setParentCategory(parentCategory);
            entity.setDeepLevel(parentCategory.getDeepLevel() + 1);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        this.categoryRepository.deleteByIdAndType(id, ECategoryType.CATEGORY.name());
        this.categoryRepository.updateCategoryParent(id);
        this.categoryRepository.updateProductCategory(id);
        return true;
    }

    @Override
    public boolean deleteByIds(List<Long> ids) {
        return false;
    }

}
