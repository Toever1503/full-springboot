package com.models;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagModel {
    private Long id;
    @NotNull
    @NotBlank
    private String tagName;
    private String slug;
}
