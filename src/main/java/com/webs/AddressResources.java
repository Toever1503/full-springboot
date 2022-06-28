package com.webs;

import com.dtos.AddressDto;
import com.dtos.ResponseDto;
import com.models.AddressModel;
import com.services.IAddressService;
import com.services.impl.AddressServiceImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.stream.Collectors;

@RestController
@RequestMapping("addresses")
@Validated
public class AddressResources {
    private final IAddressService addressService;

    public AddressResources(AddressServiceImpl addressService) {
        this.addressService = addressService;
    }

    @GetMapping
    @Transactional
    public ResponseDto getAll(Pageable page) {
        return ResponseDto.of(addressService.findAll(page).stream().map(AddressDto::toDto).collect(Collectors.toList()), "Get all addresses successfully");
    }

    @GetMapping("/{id}")
    @Transactional
    public ResponseDto getById(@PathVariable("id") Long id) {
        return ResponseDto.of(AddressDto.toDto(addressService.findById(id)), "Get address by id successfully");
    }

    @PostMapping
    @Transactional
    public ResponseDto add(@Valid @RequestBody AddressModel model) {
        model.setId(null);
        return ResponseDto.of(AddressDto.toDto(addressService.add(model)), "Address add success");
    }

    @PutMapping("{id}")
    @Transactional
    public ResponseDto update(@PathVariable long id, @Valid @RequestBody AddressModel model) {
        model.setId(id);
        return ResponseDto.of(AddressDto.toDto(addressService.update(model)), "Address update success");
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseDto delete(@PathVariable("id") Long id) {
        return ResponseDto.of(addressService.deleteById(id) ? true : null, "Delete address successfully");
    }

    @GetMapping("/provinces")
    @Transactional
    public ResponseDto getProvinces() {
        return ResponseDto.of(addressService.getAllProvince(), "Get all provinces successfully");
    }

    @GetMapping("/getAllDistrict/{provinceId}")
    @Transactional
    public ResponseDto getDistricts(@PathVariable("provinceId") Integer provinceId) {
        return ResponseDto.of(addressService.getAllDistrict(provinceId), "Get all districts successfully");
    }

    @Transactional
    @GetMapping("/getAllWards/{provinceId}/{districtId}")
    public ResponseDto getWards(@PathVariable("provinceId") Integer provinceId, @PathVariable("districtId") Integer districtId) {
        return ResponseDto.of(addressService.getAllByWard(provinceId, districtId), "Get all wards successfully");
    }
}
