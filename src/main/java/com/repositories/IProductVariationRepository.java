package com.repositories;

import com.entities.ProductVariationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IProductVariationRepository extends JpaRepository<ProductVariationEntity, Long> {
}
