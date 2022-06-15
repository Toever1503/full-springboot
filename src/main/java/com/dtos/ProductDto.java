package com.dtos;

import com.entities.OptionEntity;
import com.entities.ProductEntity;
import com.entities.ProductMetaEntity;
import com.models.OptionModel;
import com.models.ProductMetaModel;
import com.models.TagModel;
import lombok.*;
import org.json.JSONObject;

import java.util.List;
import java.util.Set;
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

    private CategoryDto category;
    private List<ProductMetaDto> productMetas;
    private List<OptionDto> options;
    private Set<TagDto> tags;


    public static ProductDto toDto(ProductEntity entity) {
        ProductDto productDto = new ProductDto();
        productDto.setId(entity.getId());
        productDto.setName(entity.getName());
        productDto.setDescription(entity.getDescription());
        productDto.setTotalQuantity(entity.getTotalQuantity());
        productDto.setTotalLike(entity.getTotalLike());
        productDto.setTotalReview(entity.getTotalReview());
        productDto.setRating(entity.getRating());
        productDto.setAvatar(entity.getImage());
        productDto.setAttachFiles(entity.getAttachFiles() != null ? new JSONObject(entity.getAttachFiles()).getJSONArray("files").toList() : null);
        productDto.setSlug(entity.getSlug());

        productDto.setCategory(entity.getCategory() == null ? null : CategoryDto.toDto(entity.getCategory(), false, false));
        productDto.setProductMetas(entity.getProductMetas().isEmpty() ? null : entity.getProductMetas().stream().map(ProductMetaDto::toDto).collect(Collectors.toList()));
        productDto.setOptions(entity.getOptions().isEmpty() ? null : entity.getOptions().stream().map(OptionDto::toDto).collect(Collectors.toList()));
        productDto.setTags(entity.getTags().isEmpty() ? null : entity.getTags().stream().map(TagDto::toTagDto).collect(Collectors.toSet()));
        return productDto;
    }
}
