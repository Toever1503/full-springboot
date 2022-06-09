package com.services;

import com.entities.ProductEntity;
import com.models.ProductModel;
import org.springframework.web.multipart.MultipartFile;

public interface IProductService extends IBaseService<ProductEntity, ProductModel, Long> {
}
