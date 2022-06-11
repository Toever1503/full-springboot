package com.models.filters;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RatingFilterModel {
    private Integer minRating;
    private Integer maxRating;
}
