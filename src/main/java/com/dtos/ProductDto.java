package com.dtos;

import com.entities.OptionEntity;
import com.entities.ProductEntity;
import com.entities.ProductMetaEntity;
import lombok.*;
import org.json.JSONObject;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ProductDto {
    private Long id;
    private String name;
    private String description;
    private Integer totalQuantity;
    private Integer totalLike;
    private Integer totalReview;
    private Integer rating;
    private String avatar;
    private List<Object> attachFiles;
    private String slug;
    private Long categoryId;
    private List<Long> productMetaId;
    private List<Long> optionsId;

    public static ProductDto toDto(ProductEntity entity){
        ProductDto productDto = new ProductDto();
        productDto.setId(entity.getId());
        productDto.setName(entity.getName());
        productDto.setDescription(entity.getDescription());
        productDto.setTotalQuantity(entity.getTotalQuantity());
        productDto.setTotalLike(entity.getTotalLike());
        productDto.setTotalReview(entity.getTotalReview());
        productDto.setRating(entity.getRating());
        productDto.setAvatar(entity.getImage());
        productDto.setAttachFiles(entity.getAttachFiles()!=null ? new JSONObject(entity.getAttachFiles()).getJSONArray("files").toList() : null);
        productDto.setSlug(entity.getSlug());
        productDto.setCategoryId(entity.getCategory().getId()==null? null : entity.getCategory().getId());
        productDto.setProductMetaId(entity.getProductMetas().stream().map(ProductMetaEntity::getId).collect(Collectors.toList()));
        productDto.setOptionsId(entity.getOptions().stream().map(OptionEntity::getId).collect(Collectors.toList()));
        return productDto;
    }
}
