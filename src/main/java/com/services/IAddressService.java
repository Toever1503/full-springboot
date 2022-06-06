package com.services;

import com.entities.Address;
import com.entities.District;
import com.entities.Province;
import com.entities.Ward;
import com.models.AddressModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IAddressService extends IBaseService<Address, AddressModel, Long> {
    List<Province> getAllProvince();
    List<District> getAllDistrict(Integer provinceId);
    List<Ward> getAllByWard(Integer provinceId ,Integer districtId);
}
