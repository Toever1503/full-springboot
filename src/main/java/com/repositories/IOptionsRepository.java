package com.repositories;

import com.entities.OptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;

@Repository
public interface IOptionsRepository extends JpaRepository<OptionEntity, Long>, JpaSpecificationExecutor<OptionEntity> {
    OptionEntity findByOptionName(String optionName);
}
