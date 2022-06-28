package com.services;

import com.entities.AddressEntity;
import com.entities.District;
import com.entities.Province;
import com.entities.Ward;
import com.models.AddressModel;

import java.util.List;
import java.util.Set;

public interface IAddressService extends IBaseService<AddressEntity, AddressModel, Long> {
    List<Province> getAllProvince();
    List<District> getAllDistrict(Integer provinceId);
    List<Ward> getAllByWard(Integer provinceId ,Integer districtId);

    Set<AddressEntity> findByUid(Long id);
}
