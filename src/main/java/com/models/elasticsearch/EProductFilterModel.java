package com.models.elasticsearch;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EProductFilterModel {
    private String q;

    private Double minPrice;
    private Double maxPrice;

    private List<String> categorySlugs;
    private String status;

    private EProductMetaFilterModel metas;

    private EProductVariationFilterModel variations;

    private List<String> tags;

    private List<String> recommendByKeywords;

    private Boolean isDeepSale = false;
}
