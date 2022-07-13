package com.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.annotation.Nonnull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OptionsModel {
    private Long id;
    private String optionKey;
    private String optionValue;
}
