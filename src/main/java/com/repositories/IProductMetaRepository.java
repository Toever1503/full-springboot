package com.repositories;

import com.entities.ProductMetaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IProductMetaRepository extends JpaRepository<ProductMetaEntity, Long> {
}
