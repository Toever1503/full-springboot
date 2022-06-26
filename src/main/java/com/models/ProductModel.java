package com.models;
import com.entities.ProductEntity;
import com.utils.ASCIIConverter;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
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

    public void setAttachFiles(List<MultipartFile> attachFiles) {
        this.attachFiles = attachFiles;
    }

    public static ProductEntity toEntity(ProductModel model){
        if(model == null) new RuntimeException("ProductModel is null");
        return ProductEntity.builder()
                .id(model.getId())
                .name(model.getName())
                .description(model.getDescription())
                .totalLike(0)
                .totalReview(0)
                .rating(0F)
                .build();
    }
}
