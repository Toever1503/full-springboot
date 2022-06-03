package com.repositories;

import com.entities.District;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IDistrictRepository extends JpaRepository<District, Integer>, JpaSpecificationExecutor<District> {
    List<District> findAllByProvince(Integer provinceId);
}
