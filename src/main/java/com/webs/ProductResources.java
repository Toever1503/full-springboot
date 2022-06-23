package com.webs;

import com.dtos.ProductDto;
import com.dtos.ResponseDto;
import com.models.ProductModel;
import com.models.filters.ProductFilter;
import com.models.specifications.ProductSpecification;
import com.services.IProductService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.codec.multipart.Part;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/products")
@Validated
public class ProductResources {
    private final IProductService productService;

    public ProductResources(IProductService productService) {
        this.productService = productService;
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @PostMapping
    public ResponseDto createProduct(@Valid @RequestPart("product") ProductModel productModel,
                                     @RequestPart("image") MultipartFile image,
                                     @RequestPart(name = "attachFiles[]", required = false) List<MultipartFile> attachFiles) {
        productModel.setId(null);
        productModel.setAttachFiles(attachFiles);
        productModel.setImage(image);
        return ResponseDto.of(ProductDto.toDto(productService.add(productModel)), "Create product successfully");
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @PutMapping("{id}")
    public ResponseDto updateProduct(@PathVariable("id") Long id, @Valid @RequestPart("product") ProductModel productModel,
                                     @RequestPart(value = "image", required = false) MultipartFile image,
                                     @RequestPart(name = "attachFiles[]", required = false) List<MultipartFile> attachFiles) {
        productModel.setId(id);
        productModel.setAttachFiles(attachFiles);
        productModel.setImage(image);
        return ResponseDto.of(ProductDto.toDto(productService.update(productModel)), "Update product successfully");
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @DeleteMapping("{id}")
    public ResponseDto deleteProduct(@PathVariable("id") Long id) {
        return ResponseDto.of(productService.deleteById(id), "Delete product successfully");
    }

    @GetMapping("/like")
    @Transactional
    public ResponseDto likeAndUnlikeProduct(@RequestParam("id") Long id) {
        int result = productService.likeProduct(id);
        if (result == 1) {
            return ResponseDto.of(true, "Liked product");
        } else {
            return ResponseDto.of(true, "Unliked product");
        }
    }

    @GetMapping("/get-all")
    @Transactional
    public ResponseDto getAllProducts(Pageable pageable) {
        return ResponseDto.of(this.productService.filter(pageable, Specification.where(ProductSpecification.byActive(true))).map(ProductDto::toDto), "Get all products");
    }

    @PostMapping("/filter")
    @Transactional
    public ResponseDto filterAllProducts(Pageable pageable, @RequestBody @Valid ProductFilter filter) {
        return ResponseDto.of(productService.filter(pageable, Specification.where(ProductSpecification.filter(filter)))
                .map(ProductDto::toDto), "Filter all products");
    }

    @GetMapping("/get-by-id/{id}")
    @Transactional
    public ResponseDto getProductById(@PathVariable("id") Long id) {
        return ResponseDto.of(ProductDto.toDto(productService.findById(id)), "Get product by id");
    }
}
