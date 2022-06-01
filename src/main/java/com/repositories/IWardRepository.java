package com.repositories;

import com.entities.Ward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IWardRepository extends JpaRepository<Ward, Integer> {
    List<Ward> findAllByProvinceAndDistrict(Integer provinceId, Integer districtId);
}
