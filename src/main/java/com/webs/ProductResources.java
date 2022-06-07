package com.webs;

import com.dtos.ProductDto;
import com.dtos.ResponseDto;
import com.services.IProductService;
import com.services.IUserLikeProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;

@RestController
@RequestMapping("/product")
public class ProductResources {
    final IProductService productService;

    public ProductResources(IUserLikeProductService userLikeProductService, IProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/like")
    @Transactional
    public ResponseDto likeAndUnlikeProduct(@RequestParam("id") Long id){
        int result = productService.likeProduct(id);
        if(result==1){
            return ResponseDto.of(true,"Liked product");
        }else{
            return ResponseDto.of(true,"Unliked product");
        }
    }

    @GetMapping
    @Transactional
    public ResponseDto getAllProducts(Pageable pageable){
        return ResponseDto.of(productService.findAll(pageable).map(ProductDto::toDto),"Get all products");
    }

    @GetMapping("/slug")
    @Transactional
    public ResponseDto getProductBySlug(@RequestParam("slug") String slug){
        return ResponseDto.of(ProductDto.toDto(productService.findProductBySlug(slug)),"Get product by slug");
    }

    @GetMapping("/{id}")
    @Transactional
    public ResponseDto getProductById(@PathVariable("id") Long id){
        return ResponseDto.of(ProductDto.toDto(productService.findById(id)),"Get product by id");
    }
}
