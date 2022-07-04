package com.repositories;

import com.entities.ProductVariationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface IProductVariationRepository extends JpaRepository<ProductVariationEntity, Long>, JpaSpecificationExecutor<ProductVariationEntity> {
    List<ProductVariationEntity> findAllByProductId(Long productId);
}
