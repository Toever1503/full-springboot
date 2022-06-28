package com.repositories;

import com.entities.CartEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ICartRepository extends JpaRepository<CartEntity, Long> {
    Optional<CartEntity> findByProduct_IdAndUser_Id(Long productId, Long userId);
    Page<CartEntity> findAllByUser_Id(Long userId, Pageable pageable);
    Optional<CartEntity> findByUser_IdAndId(Long userId, Long id);

    @Modifying
    void deleteAllByCartDetails_Empty();
}
