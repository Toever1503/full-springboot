package com.repositories;

import com.entities.ProductSkuEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;


public interface IProductSkuEntityRepository extends JpaRepository<ProductSkuEntity, Long>, JpaSpecificationExecutor<ProductSkuEntity> {

    ProductSkuEntity findByProductIdAndId(Long productId, Long skuId);

    List<ProductSkuEntity> findAllByProductId(Long id);

    List<ProductSkuEntity> deleteAllByProductId(Long id);

    void deleteAllByProductIdAndIdNotIn(Long productId, List<Long> ids);
}
