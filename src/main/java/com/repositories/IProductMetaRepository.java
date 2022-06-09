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
}
