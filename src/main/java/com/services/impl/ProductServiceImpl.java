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
import com.models.filters.ProductDtoFilter;
import com.repositories.*;
import com.services.ICategoryService;
import com.services.IProductService;
import com.utils.FileUploadProvider;
import com.utils.SecurityUtils;
import org.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchHitsImpl;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class ProductServiceImpl implements IProductService {

    private final IProductRepository productRepository;
    private final IEProductRepository eProductRepository;
    private final IProductVariationRepository productVariationRepository;
    private final IProductVariationValueRepository productVariationValueRepository;
    private final IProductSkuEntityRepository productSkuEntityRepository;

    private final ICategoryService categoryService;
    private final FileUploadProvider fileUploadProvider;
    private final IUserLikeProductRepository userLikeProductRepository;


    public ProductServiceImpl(IProductRepository productRepository,
                              IEProductRepository eProductRepository,
                              IProductVariationRepository productVariationRepository,
                              IProductVariationValueRepository productVariationValueRepository,
                              IProductSkuEntityRepository productSkuEntityRepository,
                              ICategoryService categoryService,
                              FileUploadProvider fileUploadProvider,
                              IUserLikeProductRepository userLikeProductRepository) {
        this.productRepository = productRepository;
        this.eProductRepository = eProductRepository;
        this.productVariationRepository = productVariationRepository;
        this.productVariationValueRepository = productVariationValueRepository;
        this.productSkuEntityRepository = productSkuEntityRepository;
        this.categoryService = categoryService;
        this.fileUploadProvider = fileUploadProvider;
        this.userLikeProductRepository = userLikeProductRepository;
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
    public ProductDto saveDtoOnElasticsearch(ProductEntity productEntity) {
        try {
            return eProductRepository.save(ProductDto.toDto(productEntity));
        } catch (Exception e) {
            throw new RuntimeException("Error occurred when saving product on elasticsearch");
        }
    }

    @Override
    public Page<ProductDto> findAllDto(Pageable page) {
        return this.eProductRepository.findAll(page);
    }

    @Override
    public ProductDto findDtoById(Long id) {
        return this.eProductRepository.findById(id).orElseThrow(() -> new RuntimeException("Cannot found product id: ".concat(id.toString())));
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
    public Page<ProductDto> search(Pageable page, String q) {
//        QuerydslPredicateExecutor

        return this.eProductRepository.findByNameLike(q, page);
    }

    @Override
    public Page<ProductDto> filterSearch(Pageable page, ProductDtoFilter q) {
//        SearchPage
        return null;
    }

    @Override
    public DetailProductDto findDetailProductById(Pageable similarPage, Long id) {
        ProductDto productDto = this.findDtoById(id);
        String[] fields = Arrays.stream(productDto.getClass().getDeclaredFields()).map(Field::getName).toArray(String[]::new);
        return DetailProductDto.builder()
                .data(productDto)
                .similarProducts(this.eProductRepository.searchSimilar(productDto, fields, similarPage))
                .build();
    }

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
        int size = productVariationValueRepository.checkVariationValueExist(model.getVariationValues()).size();
        if (size != model.getVariationValues().size())
            throw new RuntimeException("variation values not enough, expected " + size);
        try {
            skuEntity.setIsValid(true);
            skuEntity.setVariationSize(size);
            return this.productSkuEntityRepository.save(skuEntity);
        } catch (Exception e) {
            System.out.println("=============exception");
            throw new RuntimeException("Duplicate skuCode: " + skuEntity.getSkuCode().concat(", product id: ".concat(entity.getId().toString().concat(". please check again!"))));
        }
    }

}
