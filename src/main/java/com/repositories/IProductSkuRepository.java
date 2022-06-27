package com.repositories;

import com.entities.ProductSkuEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IProductSkuRepository extends JpaRepository<ProductSkuEntity, Long> {
}
