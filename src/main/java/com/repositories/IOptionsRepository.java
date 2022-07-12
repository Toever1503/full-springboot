package com.repositories;

import com.entities.OptionsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IOptionsRepository extends JpaRepository<OptionsEntity, Long> {
    Optional<OptionsEntity> findByOptionKey(String optionKey);
}
