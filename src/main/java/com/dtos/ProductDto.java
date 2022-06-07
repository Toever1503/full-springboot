package com.dtos;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ProductDto {
    private Long id;
    private String name;
    private String description;
    private Long totalQuantity;
    private Long totalLike;
    private Long totalReview;
    private Long rating;
    private String avater;
    private String attachFiles;
    private String slugs;
    private Long categoryId;
    private List<Long> productMetaId;
    private List<Long> optionsId;
}
