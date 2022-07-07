package com.dtos;

import com.entities.BannerEntity;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BannerDto {
    private Long id;
    private String name;
    private String attachFiles;
    private String status;

    public static BannerDto toDto(BannerEntity entity) {
        if(entity == null) return null;
        return BannerDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .status(entity.getStatus())
                .build();
    }
}
