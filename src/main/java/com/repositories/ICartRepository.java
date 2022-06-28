package com.repositories;

import com.entities.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

@Repository
public interface ICartRepository extends JpaRepository<CartEntity, Long> {
    @Modifying
    void deleteAllByCartDetails_Empty();
}

