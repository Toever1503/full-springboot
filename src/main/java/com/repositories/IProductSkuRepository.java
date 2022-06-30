package com.repositories;

import com.entities.ProductSkuEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IProductSkuRepository extends JpaRepository<ProductSkuEntity, Long> {
    List<ProductSkuEntity> findByProduct_Id(Long id);

}
