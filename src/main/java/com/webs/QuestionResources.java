package com.webs;

import com.dtos.QuestionDto;
import com.dtos.ResponseDto;
import com.entities.QuestionEntity;
import com.models.QuestionModel;
import com.services.IQuestionService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/question")
public class QuestionResources {
    private final IQuestionService questionService;

    public QuestionResources(IQuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping
    @Transactional
    public ResponseDto getQuestions(Pageable pageable){
        return ResponseDto.of(questionService.findAll(pageable).map(QuestionDto::toDto),"Get questions successfully");
    }

    @Transactional
    @GetMapping("/{id}")
    public ResponseDto getQuestionById(@PathVariable Long id){
        return ResponseDto.of(questionService.findById(id),"Get question by id successfully");
    }

    @Transactional
    @PostMapping
    public ResponseDto addQuestion(@Valid QuestionModel questionModel) {
        QuestionEntity questionEntity = this.questionService.add(questionModel);
        QuestionDto questionDto = QuestionDto.toDto(questionEntity);
        return ResponseDto.of(questionDto,"Add question successfully");
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
