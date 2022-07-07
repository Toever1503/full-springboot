package com.dtos;

import com.entities.BannerEntity;
import lombok.*;

import java.util.Date;
import java.util.List;

import static com.dtos.QuestionDto.parseJson;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BannerDto {
    private Long id;
    private String name;
    private List<Object> attachFiles;
    private Date createdDate;
    private Date updatedDate;
    private String status;
    private Boolean isEdit;
    private UserDto createdBy;

    public static BannerDto toDto(BannerEntity entity) {
        if(entity == null) return null;
        return BannerDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .attachFiles(entity.getAttachFiles() == null ? null : parseJson(entity.getAttachFiles()).getJSONArray("files").toList())
                .createdDate(entity.getCreatedDate())
                .updatedDate(entity.getUpdatedDate())
                .isEdit(entity.getIsEdit())
                .createdBy(UserDto.toDto(entity.getCreatedBy()))
                .status(entity.getStatus())
                .build();
    }
}
