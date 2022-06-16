package com.dtos;

import com.entities.Address;
import com.entities.District;
import com.entities.Province;
import com.entities.Ward;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AddressDto {
    private Long id;

    private Province province;

    private District district;

    private Ward ward;

    private String street;
    private String receiver;
    private String phone;

    public static AddressDto toDto(Address address) {
        if(address == null) return null;
        return AddressDto.builder()
                .id(address.getId())
                .province(address.getProvince())
                .district(address.getDistrict())
                .ward(address.getWard())
                .street(address.getStreet())
                .receiver(address.getReceiver())
                .phone(address.getPhone())
                .build();
    }
}
