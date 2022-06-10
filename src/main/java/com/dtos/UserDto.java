package com.dtos;
import com.entities.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String userName;
    private String email;
    private String fullName;
    private String avatar;
    private String phone;
    private String sex;
    private Date birthDate;
    private boolean status;
    private Long mainAddress;
    private java.util.Date createdDate;
    private java.util.Date updatedDate;
    public static UserDto toDto(UserEntity userEntity) {
        return UserDto.builder()
                .id(userEntity.getId())
                .userName(userEntity.getUserName())
                .email(userEntity.getEmail())
                .phone(userEntity.getPhone())
                .sex(userEntity.getSex())
                .fullName(userEntity.getFullName())
                .avatar(userEntity.getAvatar())
                .birthDate(userEntity.getBirthDate())
                .status(userEntity.isStatus())
                .mainAddress(userEntity.getMainAddress())
                .createdDate(userEntity.getCreatedDate())
                .updatedDate(userEntity.getUpdatedDate())
                .build();
    }
}

