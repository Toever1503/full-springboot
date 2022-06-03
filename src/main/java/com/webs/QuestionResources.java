package com.webs;

import com.dtos.QuestionDto;
import com.dtos.ResponseDto;
import com.dtos.TotalQuestionDto;
import com.entities.QuestionEntity;
import com.models.QuestionModel;
import com.models.QuestionResponseModel;
import com.services.IQuestionService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/question")
public class QuestionResources {
    final
    IQuestionService questionService;

    public QuestionResources(IQuestionService questionService) {
        this.questionService = questionService;
    }

    @Transactional
    @PatchMapping("/answer/{qid}")
    public ResponseDto answerQuestion(@PathVariable("qid") @Valid Long qid, @Valid QuestionResponseModel model) {
        if (model.getOldFiles().size() + model.getReplyFile().size() <= 3)
            return ResponseDto.of(questionService.answerQuestion(qid, model), "Answered");
        else
            return ResponseDto.of(null, "Failed! , Max image count is 3 per answer");
    }

    @Transactional
    @GetMapping("/user/{uid}")
    public ResponseDto getAllQuestionByUID(@PathVariable("uid") @Valid Long uid, Pageable pageable) {
        return ResponseDto.of(questionService.getAllQuestionByID(uid, pageable).map(QuestionDto::toDto), "Get all question by user id");
    }

    @Transactional
    @GetMapping("/user")
    public ResponseDto getAllMyQuestion(Pageable pageable) {
        return ResponseDto.of(questionService.getAllMyQuestion(pageable).map(TotalQuestionDto::toTotalQuestionDTO), "Get all my question");
    }

    @Transactional
    @GetMapping("/asked/user")
    public ResponseDto getAllAskedUser() {
        return ResponseDto.of(questionService.getAllAskedUser(), "Get all asked user");
    }

    @Transactional
    @GetMapping("/user/answered")
    public ResponseDto getAllMyAnsweredQuestion(Pageable pageable) {
        return ResponseDto.of(questionService.getAllMyAnsweredQuestion(pageable).map(TotalQuestionDto::toTotalQuestionDTO), "Get all my answered question");
    }

    @Transactional
    @GetMapping("/user/answered/{uid}")
    public ResponseDto getAllAnsweredQuestionByUID(@PathVariable("uid") @Valid Long uid, Pageable pageable) {
        return ResponseDto.of(questionService.getAllQuestionAnsweredByID(uid, pageable).map(QuestionDto::toDto), "Get all answered question by user id");
    }


    @GetMapping
    @Transactional
    public ResponseDto getQuestions(Pageable pageable) {
        return ResponseDto.of(questionService.findAll(pageable).map(QuestionDto::toDto), "Get questions successfully");
    }

    @Transactional
    @GetMapping("/{id}")
    public ResponseDto getQuestionById(@PathVariable Long id) {
        return ResponseDto.of(questionService.findById(id), "Get question by id successfully");
    }

    @Transactional
    @PostMapping
    public ResponseDto addQuestion(@Valid QuestionModel questionModel) {
        QuestionEntity questionEntity = this.questionService.add(questionModel);
        QuestionDto questionDto = QuestionDto.toDto(questionEntity);
        return ResponseDto.of(questionDto, "Add question successfully");
    }

    @Transactional
    @PutMapping
    public ResponseDto updateQuestion(@Valid QuestionModel questionModel) {
        QuestionEntity questionEntity = this.questionService.update(questionModel);
        QuestionDto questionDto = QuestionDto.toDto(questionEntity);
        return ResponseDto.of(questionDto, "Question updated successfully");
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseDto deleteQuestion(@PathVariable("id") Long id) {
        return ResponseDto.of(questionService.deleteById(id), "Question deleted successfully");
    }

    @Transactional
    @DeleteMapping("/delete-list")
    public ResponseDto DeleteQuestions(@RequestBody List<Long> ids) {
        return ResponseDto.of(questionService.deleteByIds(ids), "Questions deleted successfully");
    }


}
