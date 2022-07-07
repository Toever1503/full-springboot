package com.models;

import com.dtos.EBannerStatus;
import com.entities.BannerEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BannerModel {
    private Long id;
    private String name;
    @ApiModelProperty(notes = "attach Files banner", dataType = "List<MultipartFile>", example = "fileImage.jpg")
    private List<MultipartFile> attachFiles;
    @ApiModelProperty(notes = "List string link files old need keep", dataType = "List<String>", example = "[http://ijustforgotmypass.com, http://ijustforgotmypass1.com]")
    private List<String> attachFilesOrigin = new ArrayList<>();
    private String status;

    public static BannerEntity toEntity(BannerModel model) {
        if(model == null) return null;
        return BannerEntity.builder()
                .id(model.getId())
                .name(model.getName())
                .status(EBannerStatus.PUBLISHED.name())
                .isEdit(false)
                .build();
    }
}
