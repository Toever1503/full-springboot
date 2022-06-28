package com.services.impl;

import com.entities.*;
import com.models.AddressModel;
import com.repositories.*;
import com.services.IAddressService;
import com.utils.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AddressServiceImpl implements IAddressService {
    private final IAddressRepository addressRepository;
    private final IProvinceRepository provinceRepository;
    private final IDistrictRepository districtRepository;
    private final IWardRepository wardRepository;

    private final IUserRepository userRepository;

    public AddressServiceImpl(IAddressRepository addressRepository, IProvinceRepository provinceRepository, IDistrictRepository districtRepository, IWardRepository wardRepository, IUserRepository userRepository) {
        this.addressRepository = addressRepository;
        this.provinceRepository = provinceRepository;
        this.districtRepository = districtRepository;
        this.wardRepository = wardRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<AddressEntity> findAll() {
        return null;
    }

    @Override
    public Page<AddressEntity> findAll(Pageable page) {
        return this.addressRepository.findAll(page);
    }

    @Override
    public List<AddressEntity> findAll(Specification<AddressEntity> specs) {
        return null;
    }

    @Override
    public Page<AddressEntity> filter(Pageable page, Specification<AddressEntity> specs) {
        return null;
    }

    @Override
    public AddressEntity findById(Long id) {
        return addressRepository.findById(id).orElseThrow(() -> new RuntimeException("Address not found"));
    }

    @Override
    public AddressEntity add(AddressModel model) {
        Province province = provinceRepository.findById(model.getProvinceId()).orElseThrow(() -> new RuntimeException("Province not found, id: " + model.getProvinceId()));
        District district = districtRepository.findById(model.getDistrictId()).orElseThrow(() -> new RuntimeException("District not found, id: " + model.getDistrictId()));
        Ward ward = wardRepository.findById(model.getWardId()).orElseThrow(() -> new RuntimeException("Ward not found, id: " + model.getWardId()));
        AddressEntity addressEntity = AddressEntity.builder()
                .street(model.getStreet())
                .province(province)
                .district(district)
                .ward(ward)
                .phone(model.getPhone())
                .receiver(model.getReceiver())
                .user(SecurityUtils.getCurrentUser().getUser())
                .build();
        return addressRepository.save(addressEntity);
    }

    @Override
    public List<AddressEntity> add(List<AddressModel> model) {
        return model.stream().map(this::add).collect(Collectors.toList());
    }

    @Override
    public AddressEntity update(AddressModel model) {
        Province province = provinceRepository.findById(model.getProvinceId()).orElseThrow(() -> new RuntimeException("Province not found, id: " + model.getProvinceId()));
        District district = districtRepository.findById(model.getDistrictId()).orElseThrow(() -> new RuntimeException("District not found, id: " + model.getDistrictId()));
        Ward ward = wardRepository.findById(model.getWardId()).orElseThrow(() -> new RuntimeException("Ward not found, id: " + model.getWardId()));
        AddressEntity addressEntity = AddressEntity.builder()
                .id(model.getId())
                .street(model.getStreet())
                .province(province)
                .district(district)
                .ward(ward)
                .phone(model.getPhone())
                .receiver(model.getReceiver())
                .user(this.userRepository.findById(model.getUserId()).get())
                .build();
        return addressRepository.save(addressEntity);
    }

    @Override
    public boolean deleteById(Long id) {
        if(this.findById(id).getUser().getId().equals(SecurityUtils.getCurrentUserId())) {
            UserEntity currentUser = SecurityUtils.getCurrentUser().getUser();
            if(Objects.equals(currentUser.getMainAddress(), id)){
                currentUser.setMainAddress(null);
                userRepository.save(currentUser);
            }
            this.addressRepository.deleteById(id);
            return true;
        }else if (SecurityUtils.hasRole(RoleEntity.ADMINISTRATOR)){
            if (userRepository.findByMainAddress(id).isPresent()) {
                UserEntity currentUser = userRepository.findByMainAddress(id).get();
                currentUser.setMainAddress(null);
                userRepository.save(currentUser);
            }
            this.addressRepository.deleteById(id);
            return true;
        }else
        return false;
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

    @Override
    public Set<AddressEntity> findByUid(Long id) {
        return addressRepository.findAllByUser_Id(id);
    }
}
