package com.repositories;

import com.entities.ProductEntity;
import com.entities.ProductVariationEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IProductRepository extends JpaRepository<ProductEntity, Long>, JpaSpecificationExecutor<ProductEntity> {

    @Query("select distinct p.variationName from ProductVariationEntity p")
    List<String> findVariations(Pageable page);

    @Query("select distinct p.value from ProductVariationValueEntity p where p.variation.variationName = ?1")
    List<String> findVariationValues(String variationName, Pageable page);

    @Query("select distinct p.metaKey from ProductMetaEntity p")
    List<String> findMetas(Pageable page);

    @Query("select distinct p.metaValue from ProductMetaEntity p where p.metaKey = ?1")
    List<String> findMetaValues(String metaKey, Pageable page);

}
