package com.services;

import com.dtos.ProductDto;
import com.entities.ProductEntity;
import com.models.ProductModel;
import org.apache.catalina.LifecycleState;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IProductService extends IBaseService<ProductEntity, ProductModel, Long> {
    int likeProduct(Long id);

}
