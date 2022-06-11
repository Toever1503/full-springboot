package com.webs;

import com.dtos.ProductDto;
import com.dtos.ResponseDto;
import com.models.ProductModel;
import com.services.IProductService;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public ResponseDto createProduct(@RequestPart("product") ProductModel productModel, @RequestPart("image") MultipartFile image, @RequestPart(name = "attachFiles", required = false) List<MultipartFile> attachFiles) {
        productModel.setId(null);
        productModel.setAttachFiles(attachFiles);
        productModel.setImage(image);
        return ResponseDto.of(ProductDto.toDto(productService.add(productModel)), "Create product successfully");
    }

   @Transactional(rollbackFor = RuntimeException.class)
    @PutMapping("{id}")
    public ResponseDto updateProduct(@PathVariable("id") Long id, @RequestPart("product") ProductModel productModel, @RequestPart("image") MultipartFile image, @RequestPart(name = "attachFiles", required = false) List<MultipartFile> attachFiles) {
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
   @Transactional(rollbackFor = RuntimeException.class)
    public ResponseDto likeAndUnlikeProduct(@RequestParam("id") Long id){
        int result = productService.likeProduct(id);
        if (result == 1) {
            return ResponseDto.of(true, "Liked product");
        } else {
            return ResponseDto.of(true, "Unliked product");
        }
    }

    @GetMapping
   @Transactional(rollbackFor = RuntimeException.class)
    public ResponseDto getAllProducts(Pageable pageable){
        return ResponseDto.of(productService.findAll(pageable).map(ProductDto::toDto),"Get all products");
    }

    @GetMapping("/slug")
   @Transactional(rollbackFor = RuntimeException.class)
    public ResponseDto getProductBySlug(@RequestParam("slug") String slug){
        return ResponseDto.of(ProductDto.toDto(productService.findProductBySlug(slug)),"Get product by slug");
    }

    @GetMapping("/{id}")
   @Transactional(rollbackFor = RuntimeException.class)
    public ResponseDto getProductById(@PathVariable("id") Long id){
        return ResponseDto.of(ProductDto.toDto(productService.findById(id)),"Get product by id");
    }
}
