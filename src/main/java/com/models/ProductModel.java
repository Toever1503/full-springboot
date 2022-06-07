package com.models;
import com.entities.ProductEntity;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

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
    private Long totalQuantity;
    private Long totalLike;
    private Long totalReview;
    private Long rating;
    private MultipartFile image;
    private List<MultipartFile> attachFiles;
    private String slug;
    private Long categoryId;
    private List<Long> productMetaId;
    private List<Long> optionsId;

    public static ProductEntity toEntity(ProductModel model){
        if(model == null) return null;
        return ProductEntity.builder()
                .build();
    }
}
