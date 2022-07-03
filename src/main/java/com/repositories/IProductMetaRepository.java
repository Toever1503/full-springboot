package com.repositories;

import com.entities.ProductEntity;
import com.entities.ProductMetaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IProductMetaRepository extends JpaRepository<ProductMetaEntity, Long>, JpaSpecificationExecutor<ProductMetaEntity> {

    @Query("SELECT distinct pm.metaKey from ProductMetaEntity pm join ProductEntity p on pm.productId = p.id where p.industry.id = ?1")
    List<String> findAllMetaKeysByProductIndustryId(Long industryId);

    @Query("select distinct pm.metaValue from ProductMetaEntity pm where pm.metaKey = ?1")
    List<String> findAllMetaValuesByMetaKey(String metaKey);
}
