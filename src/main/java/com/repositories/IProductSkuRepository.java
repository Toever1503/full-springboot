package com.repositories;

import com.entities.ProductSkuEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IProductSkuRepository extends JpaRepository<ProductSkuEntity,Long> {
}
