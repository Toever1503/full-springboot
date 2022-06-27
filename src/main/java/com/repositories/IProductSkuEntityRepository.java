package com.repositories;

import com.entities.ProductSkuEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface IProductSkuEntityRepository extends JpaRepository<ProductSkuEntity, Long> {

    ProductSkuEntity findByProductIdAndId(Long productId, Long skuId);
}
