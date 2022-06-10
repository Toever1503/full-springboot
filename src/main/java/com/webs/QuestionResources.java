package com.webs;

import com.dtos.EStatusQuestion;
import com.dtos.QuestionDto;
import com.dtos.ResponseDto;
import com.dtos.TotalQuestionDto;
import com.entities.QuestionEntity;
import com.models.QuestionModel;
import com.models.QuestionResponseModel;
import com.services.IQuestionService;
import com.utils.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/questions")
@Validated
public class QuestionResources {
    private final
    IQuestionService questionService;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public QuestionResources(IQuestionService questionService) {
        this.questionService = questionService;
    }

    @RolesAllowed("ADMINISTRATOR")
   @Transactional(rollbackFor = RuntimeException.class)
    @PatchMapping("/answer/{qid}")
    public ResponseDto answerQuestion(@PathVariable("qid") @Valid Long qid, @Valid QuestionResponseModel model) {
        log.info("admin {%s} is answering question id: {%d}", SecurityUtils.getCurrentUser().getUsername(), qid);
        int count = 0;
        if (model.getOldFiles() != null)
            count += model.getOldFiles().size();
        if (model.getReplyFile() != null) {
            count += model.getReplyFile().size();
        }
        if (count > 3)
            return ResponseDto.of(null, "Failed! , Max image count is 3 per question");
        else
            return ResponseDto.of(TotalQuestionDto.toTotalQuestionDTO(questionService.answerQuestion(qid, model)), "Answered");
    }

   @Transactional(rollbackFor = RuntimeException.class)
    @GetMapping("/user/{uid}")
    public ResponseDto getAllQuestionByUID(@PathVariable("uid") @Valid Long uid, Pageable pageable) {
        log.info("user {%s} is getting detail question id: {%d}", SecurityUtils.getCurrentUser().getUsername(), uid);
        return ResponseDto.of(questionService.getAllQuestionByID(uid, pageable).map(TotalQuestionDto::toTotalQuestionDTO), "Get all question by user id");
    }

   @Transactional(rollbackFor = RuntimeException.class)
    @GetMapping("/user")
    public ResponseDto getAllMyQuestion(Pageable pageable) {
        log.info("user {%s} is getting all their question", SecurityUtils.getCurrentUser().getUsername());
        return ResponseDto.of(questionService.getAllMyQuestion(pageable).map(QuestionDto::toDto), "Get all my question");
    }

   @Transactional(rollbackFor = RuntimeException.class)
    @GetMapping("/asked/user")
    public ResponseDto getAllAskedUser() {
        log.info("user {%s} is creating ask question", SecurityUtils.getCurrentUser().getUsername());
        return ResponseDto.of(questionService.getAllAskedUser(), "Get all asked user");
    }

   @Transactional(rollbackFor = RuntimeException.class) // will need review later
    @GetMapping("/user/answered")
    public ResponseDto getAllMyAnsweredQuestion(Pageable pageable) {
        log.info("admin {%s} is answering question id", SecurityUtils.getCurrentUser().getUsername());
        return ResponseDto.of(questionService.getAllMyAnsweredQuestion(pageable).map(TotalQuestionDto::toTotalQuestionDTO), "Get all my answered question");
    }

   @Transactional(rollbackFor = RuntimeException.class) // will need review later
    @GetMapping("/user/answered/{uid}")
    public ResponseDto getAllAnsweredQuestionByUID(@PathVariable("uid") @Valid Long uid, Pageable pageable) {
        return ResponseDto.of(questionService.getAllQuestionAnsweredByID(uid, pageable).map(TotalQuestionDto::toTotalQuestionDTO), "Get all answered question by user id");
    }


    @RolesAllowed("ADMINISTRATOR")
    @GetMapping
   @Transactional(rollbackFor = RuntimeException.class)
    public ResponseDto getQuestions(Pageable pageable) {
        log.info("admin {%s} is getting all question", SecurityUtils.getCurrentUser().getUsername());
        return ResponseDto.of(questionService.findAll(pageable).map(TotalQuestionDto::toTotalQuestionDTO), "Get questions successfully");
    }

    @RolesAllowed("ADMINISTRATOR")
    @GetMapping("/category")
   @Transactional(rollbackFor = RuntimeException.class)
    public ResponseDto getQuestions(@RequestParam("category") EStatusQuestion category, Pageable pageable) {
        log.info("admin {%s} is getting question by category", SecurityUtils.getCurrentUser().getUsername());
        return ResponseDto.of(questionService.getAllQuestionByCategory(category.name(), pageable).map(QuestionDto::toDto), "Admin Get questions by category successfully");
    }

    @GetMapping("/user/category")
   @Transactional(rollbackFor = RuntimeException.class)
    public ResponseDto userGetQuestionsByCategory(@RequestParam("category") EStatusQuestion category, Pageable pageable) {
        log.info("user {%s} is getting question by category", SecurityUtils.getCurrentUser().getUsername());
        return ResponseDto.of(questionService.userGetAllQuestionByCategory(category.name(), pageable).map(QuestionDto::toDto), "Admin Get questions by category successfully");
    }

    @RolesAllowed("ADMINISTRATOR")
   @Transactional(rollbackFor = RuntimeException.class)
    @GetMapping("/{id}")
    public ResponseDto findById(@PathVariable Long id) {
        log.info("admin {%s} is getting detail question id", SecurityUtils.getCurrentUser().getUsername());
        return ResponseDto.of(TotalQuestionDto.toTotalQuestionDTO(questionService.findById(id)), "Get question by id successfully");
    }

   @Transactional(rollbackFor = RuntimeException.class)
    @GetMapping("user/{id}")
    public ResponseDto userGetDetailQuestion(@PathVariable Long id) {
        log.info("user {%s} is getting detail question id", SecurityUtils.getCurrentUser().getUsername());
        return ResponseDto.of(QuestionDto.toDto(questionService.getQuestionByIdAndUserId(id, SecurityUtils.getCurrentUserId())), "Get question by id successfully");
    }

    @RolesAllowed("ADMINISTRATOR")
   @Transactional(rollbackFor = RuntimeException.class)
    @PostMapping
    public ResponseDto addQuestion(@Valid QuestionModel questionModel) {
        log.info("admin {%s} is adding question", SecurityUtils.getCurrentUser().getUsername());
        if (questionModel.getQuestFile().size() > 3)
            return ResponseDto.of(null, "Failed! , Max image count is 3 per question");
        QuestionEntity questionEntity = this.questionService.add(questionModel);
        QuestionDto questionDto = QuestionDto.toDto(questionEntity);
        return ResponseDto.of(questionDto, "Add question successfully");
    }

    @RolesAllowed("ADMINISTRATOR")
   @Transactional(rollbackFor = RuntimeException.class)
    @PutMapping("{id}")
    public ResponseDto updateQuestion(@PathVariable Long id, @Valid QuestionModel questionModel) {
        log.info("admin {%s} is updating question", SecurityUtils.getCurrentUser().getUsername());
        questionModel.setId(id);
        int count = questionModel.getQuestOriginFile().size();
        if (questionModel.getQuestFile() != null)
            count += questionModel.getQuestFile().get(0).isEmpty() ? (count + questionModel.getQuestFile().size()) : 0;
        if (count > 3)
            return ResponseDto.of(null, "Failed! , Max image count is 3 per question");

        QuestionEntity questionEntity = this.questionService.update(questionModel);
        QuestionDto questionDto = QuestionDto.toDto(questionEntity);
        return ResponseDto.of(questionDto, "Question updated successfully");
    }

    @RolesAllowed("ADMINISTRATOR")
   @Transactional(rollbackFor = RuntimeException.class)
    @DeleteMapping("/{id}")
    public ResponseDto deleteQuestion(@PathVariable("id") Long id) {
        log.info("admin {%s} is deleting question id", SecurityUtils.getCurrentUser().getUsername());
        return ResponseDto.of(questionService.deleteById(id), "Question deleted successfully");
    }

    @RolesAllowed("ADMINISTRATOR")
   @Transactional(rollbackFor = RuntimeException.class)
    @DeleteMapping("/delete-list")
    public ResponseDto DeleteQuestions(@RequestBody List<Long> ids) {
        log.info("admin {%s} is deleting questions by id list {%s}", SecurityUtils.getCurrentUser().getUsername(), ids.toString());
        return ResponseDto.of(questionService.deleteByIds(ids), "Questions deleted successfully");
    }


}
