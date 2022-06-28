package com.repositories;

import com.entities.ProductVariationValueEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IProductVariationValueRepository extends JpaRepository<ProductVariationValueEntity, Long>, JpaSpecificationExecutor<ProductVariationValueEntity> {

    @Query("select pv.id from ProductVariationValueEntity pv where pv.id in ?1")
    List<Long> checkVariationValueExist(List<Long> variation);
}
