package com.dtos;

import com.entities.AddressEntity;
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

    public static AddressDto toDto(AddressEntity addressEntity) {
        if(addressEntity == null) return  null;
        return AddressDto.builder()
                .id(addressEntity.getId())
                .province(addressEntity.getProvince())
                .district(addressEntity.getDistrict())
                .ward(addressEntity.getWard())
                .street(addressEntity.getStreet())
                .receiver(addressEntity.getReceiver())
                .phone(addressEntity.getPhone())
                .build();
    }
}
