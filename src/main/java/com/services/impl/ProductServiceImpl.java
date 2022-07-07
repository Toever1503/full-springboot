package com.services.impl;

import com.config.elasticsearch.ERepositories.IEProductRepository;
import com.dtos.*;
import com.entities.*;
import com.models.ProductMetaModel;
import com.models.ProductModel;
import com.models.ProductSkuModel;
import com.models.ProductVariationModel;
import com.models.elasticsearch.EProductFilterModel;
import com.models.specifications.CategorySpecification;
import com.repositories.*;
import com.services.ICategoryService;
import com.services.IProductService;
import com.utils.FileUploadProvider;
import com.utils.SecurityUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.json.JSONObject;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.elasticsearch.index.query.QueryBuilders.*;


@Service
@Lazy
public class ProductServiceImpl implements IProductService {

    private final IProductRepository productRepository;
    private final IProductVariationRepository productVariationRepository;
    private final IProductVariationValueRepository productVariationValueRepository;
    private final IProductSkuEntityRepository productSkuEntityRepository;
    private final ICategoryService categoryService;
    private final FileUploadProvider fileUploadProvider;
    private final IUserLikeProductRepository userLikeProductRepository;

    private final IEProductRepository eProductRepository;
    private final ElasticsearchRestTemplate elasticsearchRestTemplate;

    private final Executor taskExecutor;


    public ProductServiceImpl(IProductRepository productRepository,
                              IProductVariationRepository productVariationRepository,
                              IProductVariationValueRepository productVariationValueRepository,
                              IProductSkuEntityRepository productSkuEntityRepository,
                              ICategoryService categoryService,
                              FileUploadProvider fileUploadProvider,
                              IUserLikeProductRepository userLikeProductRepository,
                              IEProductRepository eProductRepository,
                              ElasticsearchRestTemplate elasticsearchRestTemplate,
                              Executor taskExecutor) {
        this.productRepository = productRepository;
        this.productVariationRepository = productVariationRepository;
        this.productVariationValueRepository = productVariationValueRepository;
        this.productSkuEntityRepository = productSkuEntityRepository;
        this.categoryService = categoryService;
        this.fileUploadProvider = fileUploadProvider;
        this.userLikeProductRepository = userLikeProductRepository;
        this.elasticsearchRestTemplate = elasticsearchRestTemplate;
        this.eProductRepository = eProductRepository;
        this.taskExecutor = taskExecutor;
    }

    @Override
    public List<ProductEntity> findAll() {
        return this.productRepository.findAll();
    }

    @Override
    public Page<ProductEntity> findAll(Pageable page) {
        return this.productRepository.findAll(page);
    }

    @Override
    public List<ProductEntity> findAll(Specification<ProductEntity> specs) {
        return null;
    }

    @Override
    public Page<ProductEntity> filter(Pageable page, Specification<ProductEntity> specs) {
        return this.productRepository.findAll(specs, page);
    }

    @Override
    public ProductEntity findById(Long id) {
        return this.productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found, id: " + id));
    }

    @Override
    public ProductEntity add(ProductModel model) {
        ProductEntity entity = this.fromModel(model);

        entity.setRating(0f);
        entity.setTotalReview(0);
        entity.setTotalLike(0);
        entity.setTotalSold(0);


        List<CompletableFuture> futures = new ArrayList<>();

        //  save images
        String folder = this.getProductFolder(entity.getId());
        List<String> uploadedFiles = new ArrayList<>();

        //upload new file to uploadFiles and save to database
        if (model.getAttachFiles() != null) {
            futures.add(CompletableFuture.allOf(this.fileUploadProvider.asyncUploadFiles(uploadedFiles, folder, model.getAttachFiles()))
                    .thenAccept(e1 -> uploadedFiles.removeIf(e -> e == null)));
        }

        // add image
        futures.add(this.fileUploadProvider.asyncUpload1File(folder, model.getImage()).thenAccept(o -> entity.setImage(o)));

        if (!uploadedFiles.isEmpty()) {
            entity.setAttachFiles(new JSONObject(Map.of("files", uploadedFiles)).toString());
        }

        if (model.getProductMetas() != null)
            if (!model.getProductMetas().isEmpty())
                entity.setProductMetas(model.getProductMetas()
                        .stream().map(productMetaModel -> ProductMetaModel.toEntity(productMetaModel, null)).collect(Collectors.toList()));

        try {
            if (!futures.isEmpty())
                CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).get();
            entity.setAttachFiles(uploadedFiles.isEmpty() ? null : (new JSONObject(Map.of("files", uploadedFiles)).toString()));
            return this.productRepository.saveAndFlush(entity);

        } catch (InterruptedException e) {
            e.printStackTrace();
            this.taskExecutor.execute(() -> {
                removeFile(entity.getImage(), uploadedFiles);
            });
            throw new RuntimeException("Error when upload file");
        } catch (ExecutionException e) {
            e.printStackTrace();
            this.taskExecutor.execute(() -> {
                removeFile(entity.getImage(), uploadedFiles);
            });
            throw new RuntimeException("Error when upload file");
        } catch (Exception e) {
            e.printStackTrace();
            this.taskExecutor.execute(() -> {
                removeFile(entity.getImage(), uploadedFiles);
            });
            throw new RuntimeException("Error when add product");
        }
    }

    void removeFile(String file, List<String> files) {
        this.fileUploadProvider.deleteFile(file);
        files.forEach(this.fileUploadProvider::deleteFile);
    }

    @Override
    public List<ProductEntity> add(List<ProductModel> model) {
        return null;
    }


    /*
     * convert model to entity
     */
    private ProductEntity fromModel(ProductModel model) {
        CategoryEntity industry = this.categoryService.findById(model.getIndustryId());
        if (!industry.getType().equalsIgnoreCase(ECategoryType.INDUSTRY.name()))
            throw new RuntimeException("Industry is not industry, please check again");
        CategoryEntity category = this.categoryService.findById(model.getCategoryId());
        if (!category.getType().equalsIgnoreCase(ECategoryType.CATEGORY.name()))
            throw new RuntimeException("Category is not category, please check again");
        ProductEntity entity = ProductModel.toEntity(model);
        entity.setCategory(category);
        entity.setIndustry(industry);
        entity.setCreatedBy(SecurityUtils.getCurrentUser().getUser());
        return entity;
    }

    private String getProductFolder(Long id) {
        return UserEntity.FOLDER + SecurityUtils.getCurrentUsername() + ProductEntity.FOLDER + id;
    }


    @Override
    public ProductEntity update(ProductModel model) {
        ProductEntity originProduct = this.findById(model.getId());
        ProductEntity entity = this.fromModel(model);

        entity.setRating(originProduct.getRating());
        entity.setTotalReview(originProduct.getTotalReview());
        entity.setTotalLike(originProduct.getTotalLike());
        entity.setVariations(originProduct.getVariations());
        entity.setSkus(originProduct.getSkus());
        entity.setImage(originProduct.getImage());


        // update images
        //delete file into s3
        String folder = this.getProductFolder(entity.getId());
        List<Object> originalFile;
        if (!model.getAttachFilesOrigin().isEmpty()) {
            originalFile = FileUploadProvider.parseJson(originProduct.getAttachFiles());
            originalFile.removeAll(model.getAttachFilesOrigin());
            originalFile.forEach(o -> fileUploadProvider.deleteFile(o.toString()));
        }

        //add old file to uploadFiles
        List<String> uploadedFiles = new ArrayList<>();
        uploadedFiles.addAll(model.getAttachFilesOrigin());


        List<CompletableFuture> futures = new ArrayList<>();
        //upload new file to uploadFiles and save to database
        if (model.getAttachFiles() != null) {
            futures.add(CompletableFuture.allOf(this.fileUploadProvider.asyncUploadFiles(uploadedFiles, folder, model.getAttachFiles()))
                    .thenAccept(e1 -> uploadedFiles.removeIf(e -> e == null)));
//            for (MultipartFile file : model.getAttachFiles()) {
//                try {
//                    uploadedFiles.add(fileUploadProvider.uploadFile(folder, file));
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
        }


        // update image
        if (model.getImage() != null) {
            fileUploadProvider.deleteFile(originProduct.getImage());
//                filePath.set(fileUploadProvider.uploadFile(folder, model.getImage()));
            futures.add(this.fileUploadProvider.asyncUpload1File(folder, model.getImage()).thenAccept(o -> originProduct.setImage(o)));
        }

        if (model.getProductMetas() != null)
            if (!model.getProductMetas().isEmpty())
                entity.setProductMetas(model.getProductMetas()
                        .stream().map(productMetaModel -> ProductMetaModel.toEntity(productMetaModel, model.getId())).collect(Collectors.toList()));

        try {
            if (!futures.isEmpty())
                CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).get();
            entity.setAttachFiles(uploadedFiles.isEmpty() ? null : (new JSONObject(Map.of("files", uploadedFiles)).toString()));

            return this.productRepository.saveAndFlush(entity);

        } catch (InterruptedException e) {
            e.printStackTrace();
            this.taskExecutor.execute(() -> {
                removeFile(entity.getImage(), uploadedFiles);
            });
            throw new RuntimeException("Error when upload file");
        } catch (ExecutionException e) {
            e.printStackTrace();
            this.taskExecutor.execute(() -> {
                removeFile(entity.getImage(), uploadedFiles);
            });
            throw new RuntimeException("Error when upload file");
        } catch (Exception e) {
            e.printStackTrace();
            this.taskExecutor.execute(() -> {
                removeFile(entity.getImage(), uploadedFiles);
            });
            throw new RuntimeException("Error when update product");
        }

    }

    @Override
    public boolean deleteById(Long id) {
        ProductEntity entity = this.findById(id);
        entity.setStatus(EProductStatus.DELETED.name());
        return this.saveDtoOnElasticsearch(this.productRepository.saveAndFlush(entity)) != null;
    }

    @Override
    public boolean deleteByIds(List<Long> ids) {
        ids.forEach(this::deleteById);
        this.findAll();
        return true;
    }

    @Override
    public int likeProduct(Long id) {
        //If OBJ is present
        ProductEntity product = this.findById(id);
        if (userLikeProductRepository.findFirstByProductIdAndUserId(id, SecurityUtils.getCurrentUserId()) != null) {
            UserLikeProductEntity userLikeProductEntity = userLikeProductRepository.findFirstByProductIdAndUserId(id, SecurityUtils.getCurrentUserId());

            userLikeProductEntity.setIsLike(!userLikeProductEntity.getIsLike());
            product.setTotalLike(product.getTotalLike() + (userLikeProductEntity.getIsLike() ? 1 : -1));
            this.productRepository.save(product);
            return 0;
        } else {
            //if OBJ not present
            UserLikeProductEntity entity = new UserLikeProductEntity();
            entity.setProductId(id);
            entity.setIsLike(true);
            entity.setUserId(SecurityUtils.getCurrentUserId());
            product.setTotalLike(product.getTotalLike() + 1);
            this.productRepository.save(product);
            userLikeProductRepository.save(entity);
            return 1;
        }

    }

    @Override
    public ProductDto saveDtoOnElasticsearch(ProductEntity entity) {
        return this.eProductRepository.save(ProductDto.toDto(entity));
    }

    @Override
    public DetailProductDto findDetailById(Pageable page, Long id) {
        ProductDto productDto = this.eProductRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found, id: ".concat(id.toString())));
        return DetailProductDto.builder()
                .data(productDto)
                .similarProducts(this.eProductRepository.searchSimilar(productDto, ProductDto.FIELDS, page))
                .build();
    }


    @Override
    public ProductEntity saveVariations(Long productId, List<ProductVariationModel> models) {
        ProductEntity entity = this.findById(productId);
        if (!entity.getIsUseVariation())
            throw new RuntimeException("Product is not use variation, id: ".concat(entity.getId().toString()));
        entity.getVariations().clear();
        entity.getVariations().addAll(this.productVariationRepository.saveAllAndFlush(models.stream().map(variation -> ProductVariationModel.toEntity(variation, entity)).collect(Collectors.toList())));

        Map<Long, ProductVariationValueEntity> variationValuesMap = entity.getVariations().stream().flatMap(variation -> variation.getVariationValues().stream())
                .collect(Collectors.toMap(ProductVariationValueEntity::getId, Function.identity()));

        checkValidSku(productId, entity.getVariations().size(), variationValuesMap);
        return entity;
    }

    public void checkValidSku(Long productID, Integer variationSize, Map<Long, ProductVariationValueEntity> variationValuesMap) {
        List<ProductSkuEntity> skus = this.productSkuEntityRepository.findAllByProductId(productID);
        if (skus.isEmpty()) return;

        // create list future check to increase performance
        CompletableFuture<ProductSkuEntity>[] futures = skus.stream().map(sku -> CompletableFuture.supplyAsync(() -> {
            if (!sku.getVariationSize().equals(variationSize))
                sku.setIsValid(false);
            else {
                final List<Long> valueIds = Arrays.stream(sku.getSkuCode().split("-")).map(Long::valueOf).collect(Collectors.toList());
                int check = 1;

                for (int i = 0; i < variationSize; ++i) {
                    if (!variationValuesMap.containsKey(valueIds.get(i))) {
                        check = -1;
                        break;
                    }
                }

                if (check == 1) // sku code is ok, then set option agains
                {
                    String optionName = valueIds.stream().map(vId -> {
                        ProductVariationValueEntity variationValue = variationValuesMap.get(vId);
                        return variationValue.getVariation().getVariationName().concat(" ").concat(variationValue.getValue());
                    }).reduce((a, b) -> a.concat(", ").concat(b)).get();
                    sku.setOptionName(optionName);
                } else
                    sku.setIsValid(false);
            }
            return sku;
        }, this.taskExecutor)).toArray(CompletableFuture[]::new);

        try {
            CompletableFuture.allOf(futures).get(); // wait for all complete futures done
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

//        skus.forEach(sku -> {
//            if (!sku.getVariationSize().equals(variationSize))
//                sku.setIsValid(false);
//            else {
//                final List<Long> valueIds = Arrays.stream(sku.getSkuCode().split("-")).map(Long::valueOf).collect(Collectors.toList());
//                int check = 1;
//
//                for (int i = 0; i < variationSize; ++i) {
//                    if (!variationValuesMap.containsKey(valueIds.get(i))) {
//                        check = -1;
//                        break;
//                    }
//                }
//
//                if (check == 1) // sku code is ok, then set option agains
//                {
//                    sku.setIsValid(true);
//                    String optionName = valueIds.stream().map(vId -> {
//                        ProductVariationValueEntity variationValue = variationValuesMap.get(vId);
//                        return variationValue.getVariation().getVariationName().concat(" ").concat(variationValue.getValue());
//                    }).reduce((a, b) -> a.concat(", ").concat(b)).get();
//                    sku.setOptionName(optionName);
//                } else
//                    sku.setIsValid(false);
//            }
//        });
        this.productSkuEntityRepository.saveAllAndFlush(skus);
    }

    @Override
    public ProductEntity saveSkus(HttpServletRequest req, Long productId, List<ProductSkuModel> models) throws RuntimeException {
        final ProductEntity entity = this.findById(productId);
        //separate 2 type: variation and not have variation
        final String folder = this.getProductFolder(entity.getId());
        entity.getSkus().clear();
        if (entity.getIsUseVariation()) {
            if (models.isEmpty())
                this.productSkuEntityRepository.deleteAllByProductId(productId);
            else
                entity.setSkus(this.productSkuEntityRepository.saveAllAndFlush(models.stream()
                        .map(sku -> saveSku(entity, folder, sku, req))
                        .collect(Collectors.toList())));
        } else {
            entity.setSkus(List.of(this.productSkuEntityRepository.saveAndFlush(saveSku(entity, folder, models.get(0), req))));
        }
        return entity;
    }


    @Override
    public Page<ProductDto> eFilter(Pageable page, EProductFilterModel model) {
//        EProductFilterModel

//        List<Object> mustFilter = new ArrayList<>();
//        Map<String, Object> query = putMap("query", putMap("bool", putMap("must", mustFilter)));

//        CriteriaQuery


        BoolQueryBuilder rootQueryBool = boolQuery();
        List<QueryBuilder> rootQueryBuilders = rootQueryBool.must();

        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(rootQueryBool)
                .withPageable(page)
                .build();

//        List<Criteria> criteriaList = new ArrayList<>();
        if (model.getCategorySlugs() != null) {
            rootQueryBuilders.add(termsQuery("category.categorySLug.keyword", model.getCategorySlugs()));
//            filter by category
        } else if (model.getIndustrySlug() != null) {
            //filter by industry
            rootQueryBuilders.add(termQuery("industry.industrySLug.keyword", model.getIndustrySlug()));
        }

        if (model.getMaxPrice() != null && model.getMinPrice() != null) {
//            filter by price
            rootQueryBuilders.add(nestedQuery("skus", boolQuery().must(
                    rangeQuery("skus.price").gte(model.getMinPrice()).lte(model.getMaxPrice())
            ), ScoreMode.None));
        } else if (model.getMinPrice() != null) {
            rootQueryBuilders.add(nestedQuery("skus", boolQuery().must(
                    rangeQuery("skus.price").gte(model.getMinPrice())
            ), ScoreMode.None));
        } else if (model.getMaxPrice() != null) {
            rootQueryBuilders.add(nestedQuery("skus", boolQuery().must(
                    rangeQuery("skus.price").lte(model.getMaxPrice())
            ), ScoreMode.None));
        }
        if (model.getStatus() != null) {
            rootQueryBuilders.add(termQuery("status", model.getStatus()));
        }


        if (model.getTags() != null) {
            //           filter by tags
            rootQueryBuilders.add(
                    termsQuery("tags", model.getTags())
            );
        }

        if (model.getMetas() != null) {
            //           filter by metas
            BoolQueryBuilder boolMeta = boolQuery();
            List<QueryBuilder> metaBools = boolMeta.must();
            metaBools.add(termsQuery("productMetas.metaKey", model.getMetas().getMetaKeys()));
            metaBools.add(termsQuery("productMetas.metaValue", model.getMetas().getMetaValues()));

            rootQueryBuilders.add(nestedQuery("productMetas", boolMeta, ScoreMode.None));
//            List<Criteria> metaCriteriaList = new ArrayList<>();
//            model.getMetas().forEach(m -> {
//                metaCriteriaList.add(Criteria.where("metas.metaKey").is(m.getMetaKey()).and("metas.metaValue").in(m.getMetaValues()));
//            });
//            if (metaCriteriaList.size() > 0) {
//                Criteria finalMeta = null;
//                for (Criteria c : metaCriteriaList) {
//                    if (finalMeta == null) {
//                        finalMeta = c;
//                    } else {
//                        finalMeta = finalMeta.and(c);
//                    }
//                }
//                if (finalMeta != null) {
//                    criteriaList.add(finalMeta);
//                }
//            }
        }

        if (model.getVariations() != null) {
            // filter by variations
            BoolQueryBuilder boolVariation = boolQuery();
            List<QueryBuilder> variationBools = boolVariation.must();

            variationBools.add(termsQuery("variations.variationName", model.getVariations().getVariationNames()));
            variationBools.add(nestedQuery("variations.values", boolQuery().must(
                    termsQuery("variations.values.value", model.getVariations().getVariationValues())
            ), ScoreMode.None));
            rootQueryBuilders.add(nestedQuery("variations", boolVariation, ScoreMode.None));

//            List<Criteria> variationCriteriaList = new ArrayList<>();
//            model.getVariations().forEach(v -> {
//                variationCriteriaList.add(Criteria.where("variations.variationName").is(v.getVariationName()).and("variations.values.value").in(v.getVariationValues()));
//            });
//            if (variationCriteriaList.size() > 0) {
//                Criteria finalVariation = null;
//                for (Criteria c : variationCriteriaList) {
//                    if (finalVariation == null) {
//                        finalVariation = c;
//                    } else {
//                        finalVariation = finalVariation.and(c);
//                    }
//                }
//                if (finalVariation != null) {
//                    criteriaList.add(finalVariation);
//                }
//            }
        }

        if (model.getQ() != null) {
            // filter by q
//            criteriaList.add(Criteria.where("name").contains(model.getQ()));
            rootQueryBuilders.add(
                    queryStringQuery(model.getQ()).analyzeWildcard(true).defaultField("*")
            );
        }

//        use later
//        if (model.getSortBy() != null) {
//            // sort by
//        } else {
//            // default sort
//        }

//        Criteria finalQuery = null;
//        for (Criteria c : criteriaList) {
//            if (finalQuery == null) {
//                finalQuery = c;
//            } else {
//                finalQuery = finalQuery.and(c);
//            }
//        }
//
//        CriteriaQuery query = new CriteriaQuery(finalQuery, page);

        SearchHits<ProductDto> searchHit = this.elasticsearchRestTemplate.search(searchQuery, ProductDto.class);
        Page<ProductDto> pageResult = new PageImpl<>(searchHit.getSearchHits().stream().map(SearchHit::getContent).collect(Collectors.toList()), page, searchHit.getTotalHits());
        return pageResult;
    }

    @Override
    public List<ProductVariationEntity> findVariations(Long id) {
        return this.productVariationRepository.findAllByProductId(id);
    }

    @Override
    public List<ProductSkuEntity> findSkus(Long id) {
        return this.productSkuEntityRepository.findAllByProductId(id);
    }


    private static ProductFilterDataDto productFilterDataDto = null;

    @Override
    public ProductFilterDataDto getFilterData() {
        if (productFilterDataDto != null) {
            return productFilterDataDto;
        }

        ProductFilterDataDto filterData = new ProductFilterDataDto();
        Pageable page = Pageable.unpaged();

        // create future for industries
        CompletableFuture<List<IndustryDto>> industries = CompletableFuture.supplyAsync(() -> {
            List<CategoryEntity> data = this.categoryService.findAll(CategorySpecification.byType(ECategoryType.INDUSTRY));
            return data
                    .stream().map(IndustryDto::toDto)
                    .collect(Collectors.toList());
        }, this.taskExecutor);

        // create future for categories
        CompletableFuture<List<CategoryDto>> categories = CompletableFuture.supplyAsync(() -> {
            List<CategoryEntity> data = this.categoryService.findAll(CategorySpecification.byType(ECategoryType.CATEGORY));
            return data.stream().map(c -> CategoryDto.toDto(c, true)).collect(Collectors.toList());
        }, this.taskExecutor);

        // create future for vatiations
        CompletableFuture<List<DetailIndustryDto.ProductVariationDto2>> variations = CompletableFuture
                .supplyAsync(() -> this.productRepository.findVariations(page)
                        .stream()
                        .map(v -> DetailIndustryDto.ProductVariationDto2.builder()
                                .variationName(v)
                                .variationValues(this.productRepository.findVariationValues(v, page))
                                .build())
                        .collect(Collectors.toList()), this.taskExecutor);

        //create future for product metas
        CompletableFuture<List<DetailIndustryDto.ProductMetaDto2>> metas = CompletableFuture
                .supplyAsync(() -> this.productRepository.findMetas(page)
                        .stream()
                        .map(m -> DetailIndustryDto.ProductMetaDto2.builder()
                                .metaKey(m)
                                .metaValues(this.productRepository.findMetaValues(m, page))
                                .build())
                        .collect(Collectors.toList()), this.taskExecutor);

        //        wait for all future to finish
        CompletableFuture all = CompletableFuture.allOf(
                industries.thenAccept(data -> filterData.setIndustryFilter(data)),
                categories.thenAccept(data -> filterData.setCategoryFilter(data)),
                variations.thenAccept(data -> filterData.setVariationFilter(data)),
                metas.thenAccept(data -> filterData.setMetaFilter(data)));

        try {
            all.get();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        productFilterDataDto = filterData;
        return productFilterDataDto;
    }

    @Transactional
    @Override
    public Page<ProductDto> findAll(Pageable page, Specification<ProductEntity> specs) {
        Page<ProductEntity> productDtoPage = this.productRepository.findAll(specs, page);
        return productDtoPage.map(ProductDto::toDto);
    }

    @Override
    public void refreshDataElasticsearch() {
        this.eProductRepository.saveAll(
                this.productRepository.findAll().stream().map(ProductDto::toDto).collect(Collectors.toList())
        );
    }

//    private Map<String, Object> putMap(String key, Object value) {
//        Map<String, Object> map = new HashMap<>();
//        map.put(key, value);
//        return map;
//    }

    private ProductSkuEntity saveSku(ProductEntity entity, String folder, ProductSkuModel model, HttpServletRequest req) {
        ProductSkuEntity skuEntity;
        if (model.getId() != null)
            skuEntity = this.productSkuEntityRepository.findById(model.getId()).orElse(null);
        else
            skuEntity = ProductSkuModel.toEntity(model, entity, entity.getIsUseVariation());
        skuEntity.setImage(model.getOriginImage());
        if (model.getImageParameter() != null) {
            String filePath;
            try {
                fileUploadProvider.deleteFile(skuEntity.getImage());
                filePath = fileUploadProvider.uploadFile(folder, req.getPart(model.getImageParameter()));
                skuEntity.setImage(filePath);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ServletException e) {
                throw new RuntimeException(e);
            }
        }

        List<ProductVariationValueEntity> values = productVariationValueRepository.checkVariationValueExist(model.getVariationValues());
        if (values.size() != model.getVariationValues().size())
            throw new RuntimeException("variation values not enough, expected " + values.size());
        try {
            skuEntity.setOptionName(values.stream().map(v -> v.getVariation().getVariationName().concat(" ".concat(v.getValue())))
                    .collect(Collectors.joining(", ")));
            skuEntity.setIsValid(true);
            skuEntity.setVariationSize(values.size());
            return this.productSkuEntityRepository.save(skuEntity);
        } catch (Exception e) {
            System.out.println("=============exception");
            throw new RuntimeException("Duplicate skuCode: " + skuEntity.getSkuCode().concat(", product id: ".concat(entity.getId().toString().concat(". please check again!"))));
        }
    }

    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        list.add("a");
        list.add("b");
        list.add("c");
        list.add(null);

        list.removeIf(e -> e == null);
        System.out.println(list.toString());
    }

}
