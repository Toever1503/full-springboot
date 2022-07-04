package com.services.impl;

import com.config.elasticsearch.ERepositories.IEProductRepository;
import com.dtos.DetailProductDto;
import com.dtos.ECategoryType;
import com.dtos.EProductStatus;
import com.dtos.ProductDto;
import com.entities.*;
import com.models.ProductMetaModel;
import com.models.ProductModel;
import com.models.ProductSkuModel;
import com.models.ProductVariationModel;
import com.models.elasticsearch.EProductFilterModel;
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
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;
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


    public ProductServiceImpl(IProductRepository productRepository,
                              IProductVariationRepository productVariationRepository,
                              IProductVariationValueRepository productVariationValueRepository,
                              IProductSkuEntityRepository productSkuEntityRepository,
                              ICategoryService categoryService,
                              FileUploadProvider fileUploadProvider,
                              IUserLikeProductRepository userLikeProductRepository,
                              IEProductRepository eProductRepository,
                              ElasticsearchRestTemplate elasticsearchRestTemplate1) {
        this.productRepository = productRepository;
        this.productVariationRepository = productVariationRepository;
        this.productVariationValueRepository = productVariationValueRepository;
        this.productSkuEntityRepository = productSkuEntityRepository;
        this.categoryService = categoryService;
        this.fileUploadProvider = fileUploadProvider;
        this.userLikeProductRepository = userLikeProductRepository;
        this.elasticsearchRestTemplate = elasticsearchRestTemplate1;
        this.eProductRepository = eProductRepository;
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
        entity.setTotalQuantity(0);
        entity.setTotalLike(0);

        if (model.getProductMetas() != null)
            if (!model.getProductMetas().isEmpty())
                entity.setProductMetas(model.getProductMetas()
                        .stream().map(productMetaModel -> ProductMetaModel.toEntity(productMetaModel, null)).collect(Collectors.toList()));

        entity = this.productRepository.save(entity);

        //  save images
        String folder = this.getProductFolder(entity.getId());
        if (model.getAttachFiles() != null) {
            List<String> filePaths = new ArrayList<>();
            for (MultipartFile file : model.getAttachFiles()) {
                try {
                    filePaths.add(fileUploadProvider.uploadFile(folder, file));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            JSONObject jsonObject = new JSONObject(Map.of("files", filePaths));
            entity.setAttachFiles(jsonObject.toString());
        }
        if (model.getImage() != null) {
            String filePath;
            try {
                filePath = fileUploadProvider.uploadFile(folder, model.getImage());
                entity.setImage(filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return entity;
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
        entity.setTotalQuantity(originProduct.getTotalQuantity());
        entity.setTotalLike(originProduct.getTotalLike());

        if (model.getProductMetas() != null)
            if (!model.getProductMetas().isEmpty())
                entity.setProductMetas(model.getProductMetas()
                        .stream().map(productMetaModel -> ProductMetaModel.toEntity(productMetaModel, model.getId())).collect(Collectors.toList()));
        entity = this.productRepository.save(entity);


        // update images
        //delete file into s3
        String folder = this.getProductFolder(entity.getId());
        List<Object> originalFile;
        if (originProduct.getAttachFiles() != null) {
            originalFile = FileUploadProvider.parseJson(originProduct.getAttachFiles());
            originalFile.removeAll(model.getAttachFilesOrigin());
            originalFile.forEach(o -> fileUploadProvider.deleteFile(o.toString()));
        }

        //add old file to uploadFiles
        List<String> uploadedFiles = new ArrayList<>();
        if (!model.getAttachFilesOrigin().isEmpty())
            uploadedFiles.addAll(model.getAttachFilesOrigin());

        //upload new file to uploadFiles and save to database
        if (model.getAttachFiles() != null) {
            for (MultipartFile file : model.getAttachFiles()) {
                try {
                    uploadedFiles.add(fileUploadProvider.uploadFile(folder, file));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        entity.setAttachFiles(uploadedFiles.isEmpty() ? null : (new JSONObject(Map.of("files", uploadedFiles)).toString()));

        // update image
        if (model.getImage() != null) {
            String filePath;
            try {
                fileUploadProvider.deleteFile(originProduct.getImage());
                filePath = fileUploadProvider.uploadFile(folder, model.getImage());
                fileUploadProvider.deleteFile(originProduct.getImage());
                originProduct.setImage(filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return entity;
    }

    @Override
    public boolean deleteById(Long id) {
        ProductEntity entity = this.findById(id);
        entity.setStatus(EProductStatus.DELETED.name());
        return this.productRepository.save(entity) != null;
    }

    @Override
    public boolean deleteByIds(List<Long> ids) {
        ids.forEach(this::deleteById);
        return true;
    }

    @Override
    public int likeProduct(Long id) {
        //If product is present
        if (userLikeProductRepository.findFirstByProductIdAndUserId(id, SecurityUtils.getCurrentUserId()) != null) {
            UserLikeProductEntity userLikeProductEntity = userLikeProductRepository.findFirstByProductIdAndUserId(id, SecurityUtils.getCurrentUserId());
            userLikeProductEntity.setIsLike(!userLikeProductEntity.getIsLike());
            return 0;
        } else {
            //if product not present
            UserLikeProductEntity entity = new UserLikeProductEntity();
            entity.setProductId(id);
            entity.setIsLike(true);
            entity.setUserId(SecurityUtils.getCurrentUserId());
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
        entity.setVariations(this.productVariationRepository.saveAll(models.stream().map(variation -> ProductVariationModel.toEntity(variation, entity)).collect(Collectors.toList())));
        return entity;
    }

    @Override
    public ProductEntity saveSkus(HttpServletRequest req, Long productId, List<ProductSkuModel> models) throws RuntimeException {
        final ProductEntity entity = this.findById(productId);
        //separate 2 type: variation and not have variation
        final String folder = this.getProductFolder(entity.getId());
        if (entity.getIsUseVariation()) {
            entity.setSkus(models.stream()
                    .map(sku -> saveSku(entity, folder, sku, req))
                    .collect(Collectors.toList()));
        } else {
            entity.setSkus(List.of(saveSku(entity, folder, models.get(0), req)));
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

//    private Map<String, Object> putMap(String key, Object value) {
//        Map<String, Object> map = new HashMap<>();
//        map.put(key, value);
//        return map;
//    }

    private ProductSkuEntity saveSku(ProductEntity entity, String folder, ProductSkuModel model, HttpServletRequest req) {
        ProductSkuEntity skuEntity = ProductSkuModel.toEntity(model, entity, entity.getIsUseVariation());
        if (model.getImageParameter() != null) {
            String filePath;
            try {
                fileUploadProvider.deleteFile(model.getOriginImage());
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

}
