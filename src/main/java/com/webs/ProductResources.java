package com.webs;


import com.config.elasticsearch.ERepositories.EProductRepository;
import com.dtos.ProductDto;
import com.dtos.ProductSkuDto;
import com.dtos.ProductVariationDto;
import com.dtos.ResponseDto;
import com.models.ProductModel;
import com.models.ProductSkuModel;
import com.models.ProductVariationModel;
import com.services.IProductService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RequestMapping("products")
@RestController
public class ProductResources {

    private final IProductService productService;
    private final EProductRepository eProductRepository;


    public ProductResources(IProductService productService, EProductRepository eProductRepository) {
        this.productService = productService;
        this.eProductRepository = eProductRepository;
    }

    @Transactional
    @GetMapping("{id}")
    public ResponseDto findProductById(@PathVariable Long id) {
        ProductDto dto = eProductRepository.save(ProductDto.toDto(this.productService.findById(id)));
        return ResponseDto.of(dto, "Get product id: ".concat(id.toString()));
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
