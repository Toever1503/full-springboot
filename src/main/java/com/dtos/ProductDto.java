package com.dtos;

import com.config.elasticsearch.ElasticsearchIndices;
import com.entities.ProductEntity;
import lombok.*;
import org.json.JSONObject;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Collections;
import java.util.Date;
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

    public final static String[] FIELDS = {"name",
            "description",
            "category.categoryName",
            "category.description",
            "category.slug",
            "industry.industryName",
            "industry.slug",
            "industry.description",
            "productMetas.metaKey",
            "productMetas.metaValue",
            "tags.tagName",
            "variations.variationName",
            "variations.variationName.values.value"
    };
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

    @Field(type = FieldType.Integer, name = "totalSold")
    private Integer totalSold;

    @Field(type = FieldType.Float, name = "rating")
    private Float rating;

    @Field(type = FieldType.Text, name = "image")
    private String image;

    private List<Object> attachFiles;

    private CategoryDto category;

    private IndustryDto industry;

    @Field(type = FieldType.Nested, name = "productMetas", storeNullValue = true)
    private List<ProductMetaDto> productMetas;

    @Field(type = FieldType.Nested, name = "tags", storeNullValue = true)
    private Set<TagDto> tags;

    @Field(type = FieldType.Keyword, name = "status")
    private String status;

    @Field(type = FieldType.Keyword, name = "isUseVariation")
    private Boolean isUseVariation;

    @Field(type = FieldType.Nested, name = "variations", storeNullValue = true)
    private List<ProductVariationDto> variations;

    @Field(type = FieldType.Nested, name = "skus", storeNullValue = true)
    private List<ProductSkuDto> skus;

    private Date createdDate;
    private Date updatedDate;

//    @Field(type = FieldType.Object, name = "createdBy")
//    private UserDto createdBy;


    public static ProductDto toDto(ProductEntity entity) {
        if (entity == null) return null;
        ProductDto productDto = new ProductDto();
        productDto.setId(entity.getId());
        productDto.setName(entity.getName());
        productDto.setDescription(entity.getDescription());
        productDto.setTotalLike(entity.getTotalLike());
        productDto.setTotalReview(entity.getTotalReview());
        productDto.setRating(entity.getRating());
        productDto.setImage(entity.getImage());
        productDto.setStatus(entity.getStatus());
        productDto.setIsUseVariation(entity.getIsUseVariation());
        productDto.setCreatedDate(entity.getCreatedDate());
        productDto.setUpdatedDate(entity.getUpdatedDate());
//        productDto.createdBy = UserDto.toDto(entity.getCreatedBy());

        productDto.setAttachFiles(entity.getAttachFiles() != null ? new JSONObject(entity.getAttachFiles()).getJSONArray("files").toList() : List.of());
        productDto.setCategory(entity.getCategory() == null ? null : CategoryDto.toDto(entity.getCategory(), false));
        productDto.setIndustry(entity.getIndustry() == null ? null : IndustryDto.toDto(entity.getIndustry()));
        productDto.setProductMetas(entity.getProductMetas() == null ? null : entity.getProductMetas().stream().map(ProductMetaDto::toDto).collect(Collectors.toList()));
        productDto.setTags(entity.getTags() == null ? null : entity.getTags().stream().map(TagDto::toTagDto).collect(Collectors.toSet()));

        productDto.setVariations(entity.getVariations() == null ? Collections.EMPTY_LIST : entity.getVariations().stream().map(ProductVariationDto::toDto).collect(Collectors.toList()));
        productDto.setSkus(entity.getSkus() == null ? Collections.EMPTY_LIST : entity.getSkus().stream().map(ProductSkuDto::toDto).collect(Collectors.toList()));
        productDto.setTotalQuantity(productDto.getSkus().isEmpty() ? 0 : productDto.getSkus().stream().mapToInt(ProductSkuDto::getInventoryQuantity).sum());
        return productDto;
    }
}
