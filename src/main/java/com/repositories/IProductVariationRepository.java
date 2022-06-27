package com.repositories;

import com.entities.ProductVariationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface IProductVariationRepository extends JpaRepository<ProductVariationEntity, Long>, JpaSpecificationExecutor<ProductVariationEntity> {
}
