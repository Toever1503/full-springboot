package com.dtos;

import com.config.elasticsearch.ElasticsearchIndices;
import com.entities.ProductEntity;
import lombok.*;
import org.json.JSONObject;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data

//for elasticsearch
@Document(indexName = ElasticsearchIndices.PRODUCT_INDEX)
public class ProductDto {
    @Id
    private Long id;

    @Field(type = FieldType.Text, name = "name")
    private String name;

    @Field(type = FieldType.Text, name = "description")
    private String description;

    @Field(type = FieldType.Integer, name = "totalQuantity")
    private Integer totalQuantity;

    @Field(type = FieldType.Integer, name = "totalLike")
    private Integer totalLike;

    @Field(type = FieldType.Integer, name = "totalReview")
    private Integer totalReview;

    @Field(type = FieldType.Float, name = "rating")
    private Float rating;

    @Field(type = FieldType.Text, name = "image")
    private String image;

    private List<Object> attachFiles;

    @Field(type = FieldType.Object, name = "category")
    private CategoryDto category;

    @Field(type = FieldType.Object, name = "industry")
    private IndustryDto industry;

    @Field(type = FieldType.Nested, name = "productMetas")
    private List<ProductMetaDto> productMetas;

    @Field(type = FieldType.Nested, name = "tags")
    private Set<TagDto> tags;

    @Field(type = FieldType.Keyword, name = "status")
    private String status;

    @Field(type = FieldType.Keyword, name = "isUseVariation")
    private Boolean isUseVariation;

    @Field(type = FieldType.Nested, name = "variations")
    private List<ProductVariationDto> variations;

    @Field(type = FieldType.Nested, name = "skus")
    private List<ProductSkuDto> skus;

    @Field(type = FieldType.Object, name = "createdBy")
    private UserDto createdBy;


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
        productDto.createdBy = UserDto.toDto(entity.getCreatedBy());

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
