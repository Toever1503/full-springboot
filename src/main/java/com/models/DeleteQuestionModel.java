package com.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Data
public class DeleteQuestionModel {
    private Long id;
    private List<String> urlPath = new ArrayList<>();

}
