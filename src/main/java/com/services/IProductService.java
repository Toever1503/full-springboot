package com.services;

import com.dtos.DetailProductDto;
import com.dtos.ProductDto;
import com.dtos.ProductFilterDataDto;
import com.entities.ProductEntity;
import com.entities.ProductSkuEntity;
import com.entities.ProductVariationEntity;
import com.models.ProductModel;
import com.models.ProductSkuModel;
import com.models.ProductVariationModel;
import com.models.elasticsearch.EProductFilterModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface IProductService extends IBaseService<ProductEntity, ProductModel, Long> {
    int likeProduct(Long id);

    ProductDto saveDtoOnElasticsearch(ProductEntity entity);

    DetailProductDto findDetailById(Pageable page, Long id);
    ProductEntity saveVariations(Long productId, List<ProductVariationModel> models);

    ProductEntity saveSkus(HttpServletRequest req, Long productId, List<ProductSkuModel> models);

    Page<ProductDto> eFilter(Pageable page, EProductFilterModel model);

    List<ProductVariationEntity> findVariations(Long id);

    List<ProductSkuEntity> findSkus(Long id);

    ProductFilterDataDto getFilterData();

    Page<ProductDto> findAll(Pageable page, Specification<ProductEntity> specs);

    void refreshDataElasticsearch();

    Page<ProductEntity> findAllByCategoryId(Long categoryId, Pageable page);

    boolean deleteAllDataOnElasticsearch();

    boolean deleteIndexElasticsearch();

    boolean reindexElasticsearch();
}
