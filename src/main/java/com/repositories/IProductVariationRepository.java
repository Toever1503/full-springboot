package com.repositories;

import com.entities.ProductVariationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IProductVariationRepository extends JpaRepository<ProductVariationEntity, Long>, JpaSpecificationExecutor<ProductVariationEntity> {

    @Query("SELECT distinct pr.variationName from ProductVariationEntity pr join ProductEntity p on pr.product.id = p.id where p.industry.id = ?1")
    List<String> findAllVariationNamesByProductIndustryId(Long industryId);

    @Query("SELECT distinct pv.value from ProductVariationValueEntity pv join ProductVariationEntity pr on pr.id = pv.variation.id where pr.variationName = ?1")
    List<String> findALlVariationValuesByVariationName(String variationName);

}
