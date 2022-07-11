package com.models;

import com.dtos.EProductStatus;
import com.entities.ProductEntity;
import com.utils.ASCIIConverter;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ProductModel {
    @ApiModelProperty(notes = "Product ID", dataType = "Long", example = "1")
    private Long id;
    @ApiModelProperty(notes = "Product name", dataType = "String", example = "Product name")
    @NotNull
    @NotBlank
    private String name;
    @ApiModelProperty(notes = "Product description", dataType = "String", example = "Product description")
    @NotNull
    @NotBlank
    private String description;
    @ApiModelProperty(notes = "image product", dataType = "MUltipartFile", example = "abc.jpg")
    private MultipartFile image;
    @ApiModelProperty(notes = "list image product", dataType = "List<MultipartFile>", example = "[abc.jpg, def.jpg]")
    private List<MultipartFile> attachFiles;
    @ApiModelProperty(notes = "list link file image old", dataType = "List<String>", example = "[http://abc.jpg, http://def.jpg]")
    private List<String> attachFilesOrigin = new ArrayList<>();

    @ApiModelProperty(notes = "category Id", dataType = "Long", example = "1")
    @NotNull
    private Long categoryId;

    @ApiModelProperty(notes = "list product metas", dataType = "List<ProductMeta>", example = "Object productMetas")
    private List<ProductMetaModel> productMetas;

    @ApiModelProperty(notes = "list product tag", dataType = "List<Tag>", example = "Object product tag")
    private List<TagModel> tags;

    @ApiModelProperty(notes = "product status: PUBLISHED, DRAFTED, DELETED", dataType = "String", example = "PUBLISHED")
    @NotNull
    private EProductStatus status;

    @ApiModelProperty(notes = "whether product use variation or not", dataType = "Boolean", example = "true")
    @NotNull
    private Boolean isUseVariation;

    public void setAttachFiles(List<MultipartFile> attachFiles) {
        this.attachFiles = attachFiles;
    }

    public static ProductEntity toEntity(ProductModel model) {
        if (model == null) new RuntimeException("ProductModel is null");
        ProductEntity entity = ProductEntity.builder()
                .id(model.getId())
                .name(model.getName())
                .status(model.getStatus().name())
                .description(model.getDescription())
                .isUseVariation(model.getIsUseVariation())
                .build();
        if (model.getTags() != null)
            if (!model.getTags().isEmpty())
                entity.setTags(model.tags.stream().map(TagModel::toEntity).collect(Collectors.toSet()));
        return entity;
    }
}
