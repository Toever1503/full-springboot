package com.repositories;

import com.entities.CartEntity;
import com.entities.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ICartRepository extends JpaRepository<CartEntity, Long> {
    CartEntity findAllByProductIdAndOptionId(Long id, Long optionId);
    Page<CartEntity> findAllByUserId(Long id, Pageable pageable);
}
