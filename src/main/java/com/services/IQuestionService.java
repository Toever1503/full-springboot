package com.services;

import com.dtos.QuestionDto;
import com.dtos.QuestionResponseDto;
import com.entities.QuestionEntity;
import com.models.QuestionModel;
import com.models.QuestionResponseModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.stream.DoubleStream;

public interface IQuestionService extends IBaseService<QuestionEntity, QuestionModel, Long>{
    QuestionEntity answerQuestion(Long qid, QuestionResponseModel model);

    Page<QuestionEntity> getAllMyQuestion(Pageable page);

    Page<QuestionEntity> getAllMyAnsweredQuestion(Pageable pageable);

    Page<QuestionEntity> getAllQuestionByID(Long id,Pageable pageable);

    Page<QuestionEntity> getAllQuestionByCategory(String category, Pageable pageable);

    Page<QuestionEntity> getAllQuestionAnsweredByID(Long id,Pageable pageable);

    List<Long> getAllAskedUser();

    Page<QuestionEntity> userGetAllQuestionByCategory(String name, Pageable pageable);

    QuestionEntity getQuestionByIdAndUserId(Long id, Long currentUserId);
}
