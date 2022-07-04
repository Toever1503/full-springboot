package com.services;

import com.entities.ProductEntity;
import com.entities.ProductSkuEntity;
import com.entities.ProductVariationEntity;
import com.models.ProductModel;
import com.models.ProductSkuModel;
import com.models.ProductVariationModel;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

public interface IProductService extends IBaseService<ProductEntity, ProductModel, Long> {
    int likeProduct(Long id);
    List<ProductVariationEntity> saveVariations(Long productId, List<ProductVariationModel> models);

    List<ProductSkuEntity> saveSkus(HttpServletRequest req, Long productId, List<ProductSkuModel> models);

    List<ProductVariationEntity> findProductVariations(Long id);

    List<ProductSkuEntity> findProductSkus(Long id);
}
