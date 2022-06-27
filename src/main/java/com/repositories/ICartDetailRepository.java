package com.repositories;

import com.entities.CartDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ICartDetailRepository extends JpaRepository<CartDetailEntity, Long> {
    CartDetailEntity findBySku_Id(Long id);
}
