package com.webs;

import com.dtos.*;
import com.entities.QuestionEntity;
import com.models.filters.QuestionFilterModel;
import com.models.QuestionModel;
import com.models.QuestionResponseModel;
import com.models.specifications.QuestionSpecification;
import com.services.IQuestionService;
import com.services.ISocketService;
import com.utils.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.text.ParseException;
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
    @Transactional
    @PatchMapping("/answer/{qid}")
    public ResponseDto answerQuestion(@PathVariable("qid") @Valid Long qid, @Valid QuestionResponseModel model) {
        log.info("admin {} is answering question id: {%d}", SecurityUtils.getCurrentUser().getUsername(), qid);
        int count = 0;
        if (model.getOldFiles() != null)
            count += model.getOldFiles().size();
        if (model.getReplyFile() != null) {
            count += model.getReplyFile().size();
        }
        if (count > 3)
            return ResponseDto.of(null, "1 Câu hỏi chỉ được phép có 3 ảnh");
        else{
            QuestionEntity question = questionService.answerQuestion(qid, model);
            return ResponseDto.of(TotalQuestionDto.toTotalQuestionDTO(question), "Answered");
        }
    }

    @Transactional
    @GetMapping("/user")
    public ResponseDto getAllMyQuestion(Pageable pageable) {
        log.info("user {} is getting all their question", SecurityUtils.getCurrentUser().getUsername());
        return ResponseDto.of(questionService.getAllMyQuestion(pageable).map(QuestionDto::toDto), "Lấy toàn bộ câu hỏi của bạn");
    }

    @Transactional
    @GetMapping("/asked/user")
    public ResponseDto getAllAskedUser() {
        log.info("user {} is creating ask question", SecurityUtils.getCurrentUser().getUsername());
        return ResponseDto.of(questionService.getAllAskedUser(), "Lấy toàn bộ người dùng đặt câu hỏi");
    }

    @Transactional // will need review later
    @GetMapping("/user/answered")
    public ResponseDto getAllMyAnsweredQuestion(Pageable pageable) {
        log.info("admin {} is answering question id", SecurityUtils.getCurrentUser().getUsername());
        return ResponseDto.of(questionService.getAllMyAnsweredQuestion(pageable).map(TotalQuestionDto::toTotalQuestionDTO), "Lấy toàn bộ câu hỏi đã được trả lời");
    }

    @Transactional // will need review later
    @GetMapping("/user/answered/{uid}")
    public ResponseDto getAllAnsweredQuestionByUID(@PathVariable("uid") @Valid Long uid, Pageable pageable) {
        return ResponseDto.of(questionService.getAllQuestionAnsweredByID(uid, pageable).map(TotalQuestionDto::toTotalQuestionDTO), "Lấy toàn bộ câu hỏi đã được trả lời theo id người dùng:" + uid);
    }


    @RolesAllowed("ADMINISTRATOR")
    @GetMapping
    @Transactional
    public ResponseDto getQuestions(Pageable pageable) {
        log.info("admin {} is getting all question", SecurityUtils.getCurrentUser().getUsername());
        return ResponseDto.of(questionService.findAll(pageable).map(TotalQuestionDto::toTotalQuestionDTO), "Lấy toàn bộ câu hỏi");
    }

    @RolesAllowed("ADMINISTRATOR")
    @GetMapping("/category")
    @Transactional
    public ResponseDto getQuestions(@RequestParam("category") EStatusQuestion category, Pageable pageable) {
        log.info("admin {} is getting question by category", SecurityUtils.getCurrentUser().getUsername());
        return ResponseDto.of(questionService.getAllQuestionByCategory(category.name(), pageable).map(QuestionDto::toDto), "Admin lấy câu hỏi theo danh mục");
    }

    @GetMapping("/user/category")
    @Transactional
    public ResponseDto userGetQuestionsByCategory(@RequestParam("category") EStatusQuestion category, Pageable pageable) {
        log.info("user {} is getting question by category", SecurityUtils.getCurrentUser().getUsername());
        return ResponseDto.of(questionService.userGetAllQuestionByCategory(category.name(), pageable).map(QuestionDto::toDto), "Người dùng lấy câu hỏi theo danh mục");
    }

    @Transactional
    @GetMapping("/{id}")
    public ResponseDto findById(@PathVariable Long id) {
        log.info("admin {} is getting detail question id", SecurityUtils.getCurrentUser().getUsername());
        return ResponseDto.of(TotalQuestionDto.toTotalQuestionDTO(questionService.findById(id)), "Lấy câu hỏi theo id: "+id);
    }

    @Transactional
    @GetMapping("user/{id}")
    public ResponseDto userGetDetailQuestion(@PathVariable Long id) {
        log.info("user {} is getting detail question id", SecurityUtils.getCurrentUser().getUsername());
        return ResponseDto.of(QuestionResponseDto.toDto(questionService.getQuestionByIdAndUserId(id, SecurityUtils.getCurrentUserId())), "Lấy câu hỏi theo id: "+id);
    }

    @Transactional
    @PostMapping
    public ResponseDto addQuestion(@Valid QuestionModel questionModel) {
        log.info("admin {} is adding question", SecurityUtils.getCurrentUser().getUsername());

        if (questionModel.getQuestFile() != null) {
            if (questionModel.getQuestFile().size() > 3)
                return ResponseDto.of(null, "1 Câu hỏi chỉ được phép có 3 ảnh");
        }

        QuestionEntity questionEntity = this.questionService.add(questionModel);
        QuestionDto questionDto = QuestionDto.toDto(questionEntity);
        return ResponseDto.of(questionDto, "Thêm câu hỏi");
    }

    @Transactional
    @PutMapping("{id}")
    public ResponseDto updateQuestion(@PathVariable Long id, @Valid QuestionModel questionModel) {
        log.info("admin {} is updating question", SecurityUtils.getCurrentUser().getUsername());
        questionModel.setId(id);
        int count = questionModel.getQuestOriginFile().size();
        if (questionModel.getQuestFile() != null)
            count += questionModel.getQuestFile().get(0).isEmpty() ? (count + questionModel.getQuestFile().size()) : 0;
        if (count > 3)
            return ResponseDto.of(null, "1 Câu hỏi chỉ được phép có 3 ảnh");

        QuestionEntity questionEntity = this.questionService.update(questionModel);
        QuestionDto questionDto = QuestionDto.toDto(questionEntity);
        return ResponseDto.of(questionDto, "Cập nhật câu hỏi");
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseDto deleteQuestion(@PathVariable("id") Long id) {
        log.info("admin {} is deleting question id", SecurityUtils.getCurrentUser().getUsername());
        return ResponseDto.of(questionService.deleteById(id), "Xóa câu hỏi");
    }

    @Transactional
    @DeleteMapping("/delete-list")
    public ResponseDto DeleteQuestions(@RequestBody List<Long> ids) {
        log.info("admin {} is deleting questions by id list {}", SecurityUtils.getCurrentUser().getUsername(), ids.toString());
        return ResponseDto.of(questionService.deleteByIds(ids), "Xóa câu hỏi");
    }

    @Transactional
    @PostMapping("filter")
    public ResponseDto filter(@RequestBody QuestionFilterModel model, Pageable page) throws ParseException {
        log.info("{} is filtering question", SecurityUtils.getCurrentUser().getUsername());
        Page<QuestionEntity> questionEntities = questionService.filter(page, Specification.where(QuestionSpecification.filter(model)));
        return ResponseDto.of(questionEntities.map(TotalQuestionDto::toTotalQuestionDTO), "Lọc câu hỏi");
    }

    @GetMapping("list-status")
    public ResponseDto getListQuestionStatus() {
        return ResponseDto.of(EStatusQuestion.values(), "Lấy danh sách trạng thái câu hỏi");
    }

    @GetMapping("list-categories")
    public ResponseDto getListNotificationCategory() {
        return ResponseDto.of(EQuestionCategory.values(), "Lấy danh sách danh mục");
    }

}
