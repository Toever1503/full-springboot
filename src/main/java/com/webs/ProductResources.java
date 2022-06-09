package com.webs;

import com.dtos.ProductDto;
import com.dtos.ResponseDto;
import com.models.ProductModel;
import com.services.IProductService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.codec.multipart.Part;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductResources {
    private final IProductService productService;

    public ProductResources(IProductService productService) {
        this.productService = productService;
    }

    @Transactional
    @GetMapping
    public ResponseDto getProducts(Pageable pageable){
        return ResponseDto.of(productService.findAll(pageable).map(ProductDto::toDto), "Get products successfully");
    }

    @Transactional
    @GetMapping("{id}")
    public ResponseDto getProductById(@PathVariable("id") Long id){
        return ResponseDto.of(ProductDto.toDto(productService.findById(id)), "Get product successfully");
    }

    @Transactional
    @PostMapping
    public ResponseDto createProduct(@RequestPart("product") ProductModel productModel, @RequestPart("image") MultipartFile image, @RequestPart(name="attachFiles", required = false) List<MultipartFile> attachFiles){
        productModel.setId(null);
        productModel.setAttachFiles(attachFiles);
        productModel.setImage(image);
        return ResponseDto.of(ProductDto.toDto(productService.add(productModel)), "Create product successfully");
    }

    @Transactional
    @PutMapping("{id}")
    public ResponseDto updateProduct(@PathVariable("id") Long id, @RequestPart("product") ProductModel productModel, @RequestPart("image") MultipartFile image, @RequestPart(name="attachFiles", required = false) List<MultipartFile> attachFiles){
        productModel.setId(id);
        productModel.setAttachFiles(attachFiles);
        productModel.setImage(image);
        return ResponseDto.of(ProductDto.toDto(productService.update(productModel)), "Update product successfully");
    }

    @Transactional
    @DeleteMapping("{id}")
    public ResponseDto deleteProduct(@PathVariable("id") Long id){
        return ResponseDto.of(productService.deleteById(id), "Delete product successfully");
    }
}
