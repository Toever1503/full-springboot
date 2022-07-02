package com.webs;


import com.config.elasticsearch.ERepositories.IEProductRepository;
import com.dtos.ProductDto;
import com.dtos.ResponseDto;
import com.entities.RoleEntity;
import com.models.ProductModel;
import com.models.ProductSkuModel;
import com.models.ProductVariationModel;
import com.models.elasticsearch.EProductFilterModel;
import com.repositories.IProductRepository;
import com.services.IProductService;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("products")
@RestController
@Transactional
@Lazy
public class ProductResources {

    private final IProductService productService;

    private final IEProductRepository eProductRepository;
    private final IProductRepository productRepository;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public ProductResources(IProductService productService, IEProductRepository eProductRepository, IProductRepository productRepository) {
        this.productService = productService;
        this.eProductRepository = eProductRepository;
        this.productRepository = productRepository;
    }

    @GetMapping
    public ResponseDto getAll(Pageable page) {
        return ResponseDto.of(productService.findAll(page), "Get all products");
    }


    @Transactional
    @GetMapping("/{id}")
    public ResponseDto findById(@PathVariable Long id) {
        return ResponseDto.of(ProductDto.toDto(this.productService.findById(id)), "Get product id: ".concat(id.toString()));
    }


    @Transactional
    @PostMapping("variations/{productId}")
    public ResponseDto saveVariations(@PathVariable Long productId, @RequestBody @Valid List<ProductVariationModel> models) {
        ProductDto dto = productService.saveDtoOnElasticsearch(this.productService.saveVariations(productId, models));
        return ResponseDto.of(dto.getVariations(), "Save variations for product id: ".concat(productId.toString()));
    }

    @Transactional
    @PostMapping("skus/{productId}")
    public ResponseDto saveSkus(@PathVariable Long productId, @Valid @RequestPart("skus") List<ProductSkuModel> models, HttpServletRequest req) {
        ProductDto dto = productService.saveDtoOnElasticsearch(this.productService.saveSkus(req, productId, models));
        return ResponseDto.of(dto.getSkus(), "Save skus for product id: ".concat(productId.toString()));
    }

    @Transactional
    @PostMapping
    public ResponseDto createProduct(@Valid @RequestPart("product") ProductModel productModel,
                                     @RequestPart("image") MultipartFile image,
                                     @RequestPart(name = "attachFiles[]", required = false) List<MultipartFile> attachFiles) {
        productModel.setId(null);
        productModel.setAttachFiles(attachFiles);
        productModel.setImage(image);
        return ResponseDto.of(this.productService.saveDtoOnElasticsearch(productService.add(productModel)), "Create product successfully");
    }

    @Transactional
    @PutMapping("{id}")
    public ResponseDto updateProduct(@PathVariable("id") Long id, @Valid @RequestPart("product") ProductModel productModel,
                                     @RequestPart(value = "image", required = false) MultipartFile image,
                                     @RequestPart(name = "attachFiles[]", required = false) List<MultipartFile> attachFiles) {
        productModel.setId(id);
        productModel.setAttachFiles(attachFiles);
        productModel.setImage(image);
        return ResponseDto.of(this.productService.saveDtoOnElasticsearch(this.productService.update(productModel)), "Update product successfully");
    }

    @RolesAllowed(RoleEntity.ADMINISTRATOR)
    @Transactional
    @GetMapping("refreshData")
    @Operation(summary = "resync data on database to  elasticsearch")
    public String refreshElasticsearch() {
        this.eProductRepository.saveAll(this.productRepository.findAll().stream().map(ProductDto::toDto).collect(Collectors.toList()));
        return "Ok";
    }


    @Operation(summary = "filter products")
    @PostMapping("public/filter")
    public ResponseDto filterProduct(@Valid @NotNull @RequestBody EProductFilterModel filterModel, Pageable page) {
        log.info("Product filterModel: {}", filterModel);
        return ResponseDto.of(this.productService.eFilter(page, filterModel), "Filter product");
    }

    @Operation(summary = "find product by id and related data")
    @GetMapping("public/{id}")
    public ResponseDto findProductById(@PathVariable Long id, Pageable page) {
        log.info("find product by id: {}", id);
        return ResponseDto.of(
                this.productService.findDetailById(page, id),
                "Get product id: ".concat(id.toString()));
    }
}
