package com.services;

import com.entities.Address;
import com.entities.District;
import com.entities.Province;
import com.entities.Ward;
import com.models.AddressModel;

import java.util.List;
import java.util.Set;

public interface IAddressService extends IBaseService<Address, AddressModel, Long> {
    List<Province> getAllProvince();
    List<District> getAllDistrict(Integer provinceId);
    List<Ward> getAllByWard(Integer provinceId ,Integer districtId);

    Set<Address> findByUid(Long id);
}
