package com.models;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserLikeProductModel {
    @ApiModelProperty(notes = "user like product ID", dataType = "Long", example = "1")
    private Long id;
    @ApiModelProperty(notes = "product ID", dataType = "Long", example = "1")
    private Long productId;
    @ApiModelProperty(notes = "user like product", dataType = "Boolean", example = "true")
    private Boolean isLike;
}
