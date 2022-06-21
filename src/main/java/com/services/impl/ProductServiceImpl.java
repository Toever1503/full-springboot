package com.services.impl;

import com.entities.*;
import com.models.OptionModel;
import com.models.ProductMetaModel;
import com.models.ProductModel;
import com.models.TagModel;
import com.repositories.IOptionsRepository;
import com.repositories.IProductMetaRepository;
import com.repositories.IProductRepository;
import com.repositories.IUserLikeProductRepository;
import com.services.IProductService;
import com.utils.ASCIIConverter;
import com.utils.FileUploadProvider;
import com.utils.SecurityUtils;
import org.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.dtos.QuestionDto.parseJson;

@Service
public class ProductServiceImpl implements IProductService {
    private final IProductRepository productRepository;
    private final FileUploadProvider fileUploadProvider;
    private final CategoryServiceImpl categoryService;
    private final IProductMetaRepository productMetaRepository;
    private final IOptionsRepository optionsRepository;
    private final TagServiceImp tagService;
    final IUserLikeProductRepository userLikeProductRepository;

    public ProductServiceImpl(IProductRepository productRepository, FileUploadProvider fileUploadProvider,
                              CategoryServiceImpl categoryService, IProductMetaRepository productMetaRepository,
                              IOptionsRepository optionsRepository, TagServiceImp tagService,
                              IUserLikeProductRepository userLikeProductRepository) {
        this.productRepository = productRepository;
        this.fileUploadProvider = fileUploadProvider;
        this.categoryService = categoryService;
        this.productMetaRepository = productMetaRepository;
        this.optionsRepository = optionsRepository;
        this.tagService = tagService;
        this.userLikeProductRepository = userLikeProductRepository;
    }

    @Override
    public List<ProductEntity> findAll() {
        return null;
    }

    @Override
    public Page<ProductEntity> findAll(Pageable page) {
        return this.productRepository.findAll(page);
    }

    @Override
    public Page<ProductEntity> filter(Pageable page, Specification<ProductEntity> specs) {
        return productRepository.findAll(specs, page);
    }

    @Override
    public ProductEntity findById(Long id) {
        return productRepository.findByIdAndActive(id, true).orElseThrow(() -> new RuntimeException("Product not found, id: " + id));
    }

    @Override
    public ProductEntity add(ProductModel model) {
        ProductEntity productEntity = ProductModel.toEntity(model);
        Integer totalQuantity = 0;

        CategoryEntity category = categoryService.findById(model.getCategoryId());
        productEntity.setCategory(category);

        // set productMeta, option, tag
        List<ProductMetaEntity> productMetas = model.getProductMetas().stream().map(productMetaModel -> ProductMetaModel.toEntity(productMetaModel, null)).collect(Collectors.toList());
        List<OptionEntity> options = model.getOptions().stream().map(optionModel -> OptionModel.toEntity(optionModel, null)).collect(Collectors.toList());
        Set<TagEntity> tags = model.getTags().stream().map(tagModel -> TagModel.toEntity(tagModel)).collect(Collectors.toSet());
        productEntity.setOptions(options);

        for (OptionEntity option : options) {
            totalQuantity += option.getQuantity();
        }
        productEntity.setTotalQuantity(totalQuantity);

        productEntity.setProductMetas(productMetas);
        productEntity.setTags(tags);
        productEntity.setCreatedBy(SecurityUtils.getCurrentUser().getUser());
        ProductEntity savedProduct = productRepository.save(productEntity);

        String folder = UserEntity.FOLDER + SecurityUtils.getCurrentUsername() + ProductEntity.FOLDER;
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
            savedProduct.setAttachFiles(jsonObject.toString());
        }

        if (model.getImage() != null) {
            String filePath;
            try {
                filePath = fileUploadProvider.uploadFile(folder, model.getImage());
                savedProduct.setImage(filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return this.productRepository.save(savedProduct);
    }

    @Override
    public List<ProductEntity> add(List<ProductModel> model) {
        return null;
    }

    @Override
    public ProductEntity update(ProductModel model) {
        ProductEntity originProduct = this.findById(model.getId());
        final String folder = UserEntity.FOLDER + SecurityUtils.getCurrentUsername() + ProductEntity.FOLDER;

        // update total product quantity => don't yet
        originProduct.setName(model.getName());
        originProduct.setDescription(model.getDescription());
        originProduct.setTotalQuantity(0);
        originProduct.setTotalLike(0);
        originProduct.setTotalReview(0);
        originProduct.setActive(true);

        CategoryEntity category = categoryService.findById(model.getCategoryId());
        originProduct.setCategory(category);

        originProduct.getProductMetas().clear();
        originProduct.getOptions().clear();
        originProduct.getProductMetas().addAll(model.getProductMetas().stream().map(mt -> ProductMetaModel.toEntity(mt, originProduct.getId())).collect(Collectors.toList()));

        // set product option
        originProduct.getOptions().addAll(model.getOptions().stream().map(o -> OptionModel.toEntity(o, originProduct.getId())).collect(Collectors.toList()));

        // set tag
        Set<TagEntity> tags = model.getTags().stream().map(tagModel -> TagModel.toEntity(tagModel)).collect(Collectors.toSet());
        originProduct.setTags(tags);

        ProductEntity updateProduct = productRepository.save(originProduct);

        //delete file into s3
        List<Object> originalFile;
        if (originProduct.getAttachFiles() != null) {
            originalFile = (parseJson(originProduct.getAttachFiles()).getJSONArray("files").toList());
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
        updateProduct.setAttachFiles(uploadedFiles.isEmpty() ? null : (new JSONObject(Map.of("files", uploadedFiles)).toString()));
        // update image
        if (model.getImage() != null) {
            String filePath;
            try {
                filePath = fileUploadProvider.uploadFile(folder, model.getImage());
                fileUploadProvider.deleteFile(originProduct.getImage());
                originProduct.setImage(filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return this.productRepository.save(updateProduct);
    }

    @Override
    public boolean deleteById(Long id) {
        ProductEntity productEntity = this.findById(id);
        if (productEntity.getCreatedBy().getId() == SecurityUtils.getCurrentUserId() || SecurityUtils.hasRole(RoleEntity.ADMINISTRATOR)) {
            productEntity.setActive(false);
            this.productRepository.save(productEntity);
            return true;
        } else
            return false;

    }

    @Override
    public boolean deleteByIds(List<Long> ids) {
        return false;
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
}
