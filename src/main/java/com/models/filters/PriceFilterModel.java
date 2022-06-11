package com.models.filters;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PriceFilterModel {
    private Double minPrice;
    private Double maxPrice;
}
