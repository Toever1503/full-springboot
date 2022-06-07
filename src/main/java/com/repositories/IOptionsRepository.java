package com.repositories;

import com.entities.OptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IOptionsRepository extends JpaRepository<OptionEntity, Long> {
}
