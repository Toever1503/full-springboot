package com.repositories;

import com.entities.CartDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ICartDetailRepository extends JpaRepository<CartDetailEntity, Long> {
    Optional<CartDetailEntity> findCartDetailEntityByCart_IdAndSku_Id(Long cartId, Long skuId);
}
