package com.dtos;
import com.entities.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String userName;
    private String email;
    private String fullName;
    private Date birthDate;
    private boolean status;
    private Long mainAddress;
    public static UserDto toDto(UserEntity userEntity) {
        return UserDto.builder()
                .id(userEntity.getId())
                .userName(userEntity.getUserName())
                .email(userEntity.getEmail())
                .fullName(userEntity.getFullName())
                .birthDate(userEntity.getBirthDate())
                .status(userEntity.isStatus())
                .mainAddress(userEntity.getMainAddress())
                .build();
    }
}

