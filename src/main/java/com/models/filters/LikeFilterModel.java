package com.models.filters;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LikeFilterModel {
    private Integer minLike;
    private Integer maxLike;
}
