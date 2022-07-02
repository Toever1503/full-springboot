package com.webs;


import com.config.elasticsearch.ERepositories.IEProductRepository;
import com.dtos.ProductDto;
import com.dtos.ProductSkuDto;
import com.dtos.ProductVariationDto;
import com.dtos.ResponseDto;
import com.entities.ProductEntity;
import com.entities.RoleEntity;
import com.models.ProductModel;
import com.models.ProductSkuModel;
import com.models.ProductVariationModel;
import com.services.IProductService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequestMapping("products")
@RestController
@Transactional
public class ProductResources {

    private final IProductService productService;
    private final IEProductRepository eProductRepository;


    public ProductResources(IProductService productService, IEProductRepository eProductRepository) {
        this.productService = productService;
        this.eProductRepository = eProductRepository;
    }

    @GetMapping
    public ResponseDto getAll(Pageable page) {
        return ResponseDto.of(productService.findAllDto(page), "Get all products");
    }


    @GetMapping("{id}")
    public ResponseDto findProductById(@PathVariable Long id, Pageable page) {
        return ResponseDto.of(this.productService.findDetailProductById(page, id), "Get product id: ".concat(id.toString()));
    }

    @Transactional
    @GetMapping("id/{id}")
    public ResponseDto findById(@PathVariable Long id) {
        Page<ProductEntity> ps = this.productService.findAll(PageRequest.of(0, 20));
//        Hibernate.initialize(ps);
//        ProductDto dto = eProductRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        return ResponseDto.of(ps.map(ProductDto::toDto), "Get all products");
    }


    @Transactional
    @PostMapping("variations/{productId}")
    public ResponseDto saveVariations(@PathVariable Long productId, @RequestBody @Valid List<ProductVariationModel> models) {
        return ResponseDto.of(this.productService.saveDtoOnElasticsearch(this.productService.saveVariations(productId, models)).getVariations(), "Save variations for product id: ".concat(productId.toString()));
    }

    @Transactional
    @PostMapping("skus/{productId}")
    public ResponseDto saveSkus(@PathVariable Long productId, @Valid @RequestPart("skus") List<ProductSkuModel> models, HttpServletRequest req) {
        return ResponseDto.of(this.productService.saveDtoOnElasticsearch(this.productService.saveSkus(req, productId, models)).getSkus(), "Save skus for product id: ".concat(productId.toString()));
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
        return ResponseDto.of(this.productService.saveDtoOnElasticsearch(productService.update(productModel)), "Update product successfully");
    }

    @RolesAllowed(RoleEntity.ADMINISTRATOR)
    @Transactional
    @GetMapping("refreshData")
    @Operation(summary = "resync data on database to  elasticsearch")
    public String refreshElasticsearch() {
        return "Ok";
    }

    @GetMapping("_search")
    private ResponseDto search(@RequestParam @Valid @NotBlank @NotNull String q, Pageable page) {
        return ResponseDto.of(this.productService.search(page, q), "Search product");
    }

}
