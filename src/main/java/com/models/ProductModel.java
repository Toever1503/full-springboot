package com.models;
import com.entities.ProductEntity;
import com.utils.ASCIIConverter;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ProductModel {
    private Long id;
    private String name;
    private String description;
    private MultipartFile image;
    private List<MultipartFile> attachFiles;
    private List<String> attachFilesOrigin = new ArrayList<>();
    private String slug;
    private Long categoryId;
    private List<ProductMetaModel> productMetas;
    private List<OptionModel> options;
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
                .rating(0)
                .slug(model.getSlug() == null ? ASCIIConverter.utf8ToAscii(model.getName()) : ASCIIConverter.utf8ToAscii(model.getSlug()))
                .active(true)
                .build();
    }
}
