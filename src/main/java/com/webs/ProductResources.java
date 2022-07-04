package com.webs;


import com.dtos.ProductDto;
import com.dtos.ProductSkuDto;
import com.dtos.ProductVariationDto;
import com.dtos.ResponseDto;
import com.entities.RoleEntity;
import com.models.ProductModel;
import com.models.ProductSkuModel;
import com.models.ProductVariationModel;
import com.services.IProductService;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RequestMapping("products")
@RestController
public class ProductResources {

    private final IProductService productService;


    public ProductResources(IProductService productService) {
        this.productService = productService;
    }

    @Transactional
    @GetMapping
    public ResponseDto getAllProducts(Pageable pageable) {
        return ResponseDto.of(this.productService.findAll(pageable), "Get all products");
    }

    @Transactional
    @GetMapping("{id}")
    public ResponseDto findProductById(@PathVariable Long id) {
        return ResponseDto.of(ProductDto.toDto(this.productService.findById(id)), "Get product id: ".concat(id.toString()));
    }

    @Transactional
    @PostMapping("variations/{productId}")
    public ResponseDto saveVariations(@PathVariable Long productId, @RequestBody @Valid List<ProductVariationModel> models) {
        return ResponseDto.of(this.productService.saveVariations(productId, models).stream().map(ProductVariationDto::toDto), "Save variations for product id: ".concat(productId.toString()));
    }

    @Transactional
    @PostMapping("skus/{productId}")
    public ResponseDto saveSkus(@PathVariable Long productId, @Valid @RequestPart("skus") List<ProductSkuModel> models, HttpServletRequest req) {
        return ResponseDto.of(this.productService.saveSkus(req, productId, models).stream().map(ProductSkuDto::toDto), "Save skus for product id: ".concat(productId.toString()));
    }

    @Transactional
    @PostMapping
    public ResponseDto createProduct(@Valid @RequestPart("product") ProductModel productModel,
                                     @RequestPart("image") MultipartFile image,
                                     @RequestPart(name = "attachFiles[]", required = false) List<MultipartFile> attachFiles) {
        productModel.setId(null);
        productModel.setAttachFiles(attachFiles);
        productModel.setImage(image);
        return ResponseDto.of(ProductDto.toDto(productService.add(productModel)), "Create product successfully");
    }

    @RolesAllowed(RoleEntity.ADMINISTRATOR)
    @Transactional
    @GetMapping("variations/{id}")
    public ResponseDto getVariations(@PathVariable Long id){
        return ResponseDto.of(this.productService.findProductVariations(id).stream().map(ProductVariationDto::toDto), "Get variations for product id: ".concat(id.toString()));
    }

    @RolesAllowed(RoleEntity.ADMINISTRATOR)
    @Transactional
    @GetMapping("skus/{id}")
    public ResponseDto getSkus(@PathVariable Long id){
        return ResponseDto.of(this.productService.findProductSkus(id).stream().map(ProductSkuDto::toDto), "Get skus for product id: ".concat(id.toString()));
    }

    @Transactional
    @PutMapping("{id}")
    public ResponseDto updateProduct(@PathVariable("id") Long id, @Valid @RequestPart("product") ProductModel productModel,
                                     @RequestPart(value = "image", required = false) MultipartFile image,
                                     @RequestPart(name = "attachFiles[]", required = false) List<MultipartFile> attachFiles) {
        productModel.setId(id);
        productModel.setAttachFiles(attachFiles);
        productModel.setImage(image);
        return ResponseDto.of(ProductDto.toDto(productService.update(productModel)), "Update product successfully");
    }


}
