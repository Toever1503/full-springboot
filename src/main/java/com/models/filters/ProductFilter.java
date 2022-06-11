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
//    private List<Long> id_in;
//    private List<Long> id_not_in;
    private String name;
    private String slug;
    private Boolean active;
    private List<ProductMetaFilterModel> metas;


}
