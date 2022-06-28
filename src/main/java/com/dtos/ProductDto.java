package com.dtos;

import com.entities.ProductEntity;
import io.swagger.models.auth.In;
import lombok.*;
import org.json.JSONObject;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ProductDto {
    private Long id;
    private String name;
    private String description;
    private Integer totalQuantity;
    private Integer totalLike;
    private Integer totalReview;
    private Float rating;
    private String image;
    private List<Object> attachFiles;
    private CategoryDto category;
    private IndustryDto industry;
    private List<ProductMetaDto> productMetas;
    private Set<TagDto> tags;
    private String status;
    private Boolean isUseVariation;

    private List<ProductVariationDto> variations;
    private List<ProductSkuDto> skus;


    public static ProductDto toDto(ProductEntity entity) {
        if (entity == null) return null;
        ProductDto productDto = new ProductDto();
        productDto.setId(entity.getId());
        productDto.setName(entity.getName());
        productDto.setDescription(entity.getDescription());
        productDto.setTotalQuantity(entity.getTotalQuantity());
        productDto.setTotalLike(entity.getTotalLike());
        productDto.setTotalReview(entity.getTotalReview());
        productDto.setRating(entity.getRating());
        productDto.setImage(entity.getImage());
        productDto.setStatus(entity.getStatus());
        productDto.setIsUseVariation(entity.getIsUseVariation());

        productDto.setAttachFiles(entity.getAttachFiles() != null ? new JSONObject(entity.getAttachFiles()).getJSONArray("files").toList() : null);
        productDto.setCategory(entity.getCategory() == null ? null : CategoryDto.toDto(entity.getCategory(), false));
        productDto.setIndustry(entity.getIndustry() == null ? null : IndustryDto.toDto(entity.getIndustry(), false));
        productDto.setProductMetas(entity.getProductMetas() == null ? null : entity.getProductMetas().stream().map(ProductMetaDto::toDto).collect(Collectors.toList()));
        productDto.setTags(entity.getTags() == null ? null : entity.getTags().stream().map(TagDto::toTagDto).collect(Collectors.toSet()));


        productDto.setVariations(entity.getVariations() == null ? null : entity.getVariations().stream().map(ProductVariationDto::toDto).collect(Collectors.toList()));
        productDto.setSkus(entity.getSkus() == null ? null : entity.getSkus().stream().map(ProductSkuDto::toDto).collect(Collectors.toList()));
        return productDto;
    }
}
