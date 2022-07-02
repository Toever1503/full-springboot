package com.models.elasticsearch;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EProductMetaFilterModel {
    @NotNull
    @Size(min = 1)
    private List<String> metaKeys;

    @NotNull
    @Size(min = 1)
    private List<String> metaValues;
}
