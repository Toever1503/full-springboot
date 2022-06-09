package com.services.impl;

import com.entities.Address;
import com.entities.District;
import com.entities.Province;
import com.entities.Ward;
import com.models.AddressModel;
import com.repositories.IAddressRepository;
import com.repositories.IDistrictRepository;
import com.repositories.IProvinceRepository;
import com.repositories.IWardRepository;
import com.services.IAddressService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddressServiceImpl implements IAddressService {
    private final IAddressRepository addressRepository;
    private final IProvinceRepository provinceRepository;
    private final IDistrictRepository districtRepository;
    private final IWardRepository wardRepository;

    public AddressServiceImpl(IAddressRepository addressRepository, IProvinceRepository provinceRepository, IDistrictRepository districtRepository, IWardRepository wardRepository) {
        this.addressRepository = addressRepository;
        this.provinceRepository = provinceRepository;
        this.districtRepository = districtRepository;
        this.wardRepository = wardRepository;
    }

    @Override
    public List<Address> findAll() {
        return null;
    }

    @Override
    public Page<Address> findAll(Pageable page) {
        return this.addressRepository.findAll(page);
    }

    @Override
    public Page<Address> filter(Pageable page, Specification<Address> specs) {
        return null;
    }

    @Override
    public Address findById(Long id) {
        return addressRepository.findById(id).orElseThrow(() -> new RuntimeException("Address not found"));
    }

    @Override
    public Address add(AddressModel model) {
        Province province = provinceRepository.findById(model.getProvinceId()).orElseThrow(() -> new RuntimeException("Province not found, id: " + model.getProvinceId()));
        District district = districtRepository.findById(model.getDistrictId()).orElseThrow(() -> new RuntimeException("District not found, id: " + model.getDistrictId()));
        Ward ward = wardRepository.findById(model.getWardId()).orElseThrow(() -> new RuntimeException("Ward not found, id: " + model.getWardId()));
        Address address = Address.builder()
                .id(model.getId())
                .street(model.getStreet())
                .province(province)
                .district(district)
                .ward(ward)
                .phone(model.getPhone())
                .receiver(model.getReceiver())
                .build();
        return addressRepository.save(address);
    }

    @Override
    public List<Address> add(List<AddressModel> model) {
        return model.stream().map(m -> add(m)).collect(Collectors.toList());
    }

    @Override
    public Address update(AddressModel model) {
        return add(model);
    }

    @Override
    public boolean deleteById(Long id) {
        this.addressRepository.deleteById(id);
        return true;
    }

    @Override
    public boolean deleteByIds(List<Long> ids) {
        return false;
    }

    @Override
    public List<Province> getAllProvince() {
        return provinceRepository.findAll();
    }

    @Override
    public List<District> getAllDistrict(Integer provinceId) {
        return this.districtRepository.findAllByProvince(provinceId);
    }

    @Override
    public List<Ward> getAllByWard(Integer provinceId, Integer DistrictId) {
        List<Ward> ls = this.wardRepository.findAllByProvinceAndDistrict(provinceId, DistrictId);
        return ls;
    }
}
