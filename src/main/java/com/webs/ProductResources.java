package com.webs;

import com.dtos.ResponseDto;
import com.services.IUserLikeProductService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product")
public class ProductResources {
    final IUserLikeProductService userLikeProductService;

    public ProductResources(IUserLikeProductService userLikeProductService) {
        this.userLikeProductService = userLikeProductService;
    }

    @GetMapping("/like")
    public ResponseDto likeAndUnlikeProduct(@RequestParam("id") Long id){
        int result = userLikeProductService.likeProduct(id);
        if(result==1){
            return ResponseDto.of(true,"Liked product");
        }else{
            return ResponseDto.of(true,"Unliked product");
        }
    }
}
