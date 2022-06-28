package com.models.filters;

import com.models.specifications.ProductMetaFilterModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductFilter {
    private String t;
    private String name;
    private DateFilterModel date;
    private PriceFilterModel price;
    private List<String> categorySlugs;
    private List<VariationModelFilter> variations;
    private LikeFilterModel likeFilterModel;
    private ReviewFilterModel reviewFilterModel;
    private RatingFilterModel ratingFilterModel;
    private String metaKey;
    //tag
    private List<ProductMetaFilterModel> metas;

}
