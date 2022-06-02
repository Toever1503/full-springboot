package com.services;

import com.entities.QuestionEntity;
import com.models.QuestionModel;

import java.util.List;

public interface IQuestionService extends IBaseService<QuestionEntity, QuestionModel, Long>{
    public boolean deleteById(Long id, List<String> urlPath);
}
