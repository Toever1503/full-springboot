package com.repositories;

import com.entities.ProductSkuEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface IProductSkuEntityRepository extends JpaRepository<ProductSkuEntity, Long>, JpaSpecificationExecutor<ProductSkuEntity> {

    ProductSkuEntity findByProductIdAndId(Long productId, Long skuId);
}
