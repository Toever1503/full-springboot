package com.repositories;

import com.entities.Ward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IWardRepository extends JpaRepository<Ward, Integer>, JpaSpecificationExecutor<Ward> {
    List<Ward> findAllByProvinceAndDistrict(Integer provinceId, Integer districtId);
}
