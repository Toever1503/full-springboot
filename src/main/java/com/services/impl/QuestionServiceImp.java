package com.services.impl;

import com.config.FrontendConfiguration;
import com.dtos.ENotificationCategory;
import com.dtos.EStatusQuestion;
import com.entities.QuestionEntity;
import com.entities.RoleEntity;
import com.entities.UserEntity;
import com.models.QuestionModel;
import com.models.QuestionResponseModel;
import com.models.SocketNotificationModel;
import com.repositories.IQuestionRepository;
import com.repositories.IUserRepository;
import com.services.*;
import com.utils.FileUploadProvider;
import com.utils.SecurityUtils;
import org.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuestionServiceImp implements IQuestionService {
    final IQuestionRepository questionRepository;
    final IUserService userService;
    final FileUploadProvider fileUploadProvider;
    final ISocketService socketService; // remove this line later
    final INotificationService notificationService;

    private final IUserRepository userRepository;
    final MailService mailService;

    public QuestionServiceImp(IQuestionRepository questionRepository, IUserService userService, FileUploadProvider fileUploadProvider, ISocketService socketService, INotificationService notificationService, IUserRepository userRepository, MailService mailService) {
        this.questionRepository = questionRepository;
        this.userService = userService;
        this.fileUploadProvider = fileUploadProvider;
        this.socketService = socketService;
        this.notificationService = notificationService;
        this.userRepository = userRepository;
        this.mailService = mailService;
    }

    @Override
    public List<QuestionEntity> findAll() {
        return null;
    }

    @Override
    public Page<QuestionEntity> findAll(Pageable page) {
        return questionRepository.findAllWithCompatible(true, page);
    }

    @Override
    public List<QuestionEntity> findAll(Specification<QuestionEntity> specs) {
        return null;
    }

    @Override
    public Page<QuestionEntity> filter(Pageable page, Specification<QuestionEntity> specs) {
        return this.questionRepository.findAll(specs, page);
    }

    @Override
    public QuestionEntity findById(Long id) {
        return questionRepository.findById(id).orElse(null);
    }

    @Override
    public QuestionEntity add(QuestionModel model) {
        QuestionEntity questionEntity = QuestionModel.toEntity(model);
        if (model.getQuestFile() != null) {
            if (!model.getQuestFile().get(0).getOriginalFilename().equals("")) {
                List<String> filePaths = new ArrayList<>();
                for (MultipartFile file : model.getQuestFile()) {
                    try {
                        filePaths.add(fileUploadProvider.uploadFile(SecurityUtils.getCurrentUsername(), file));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                JSONObject jsonObject = new JSONObject(Map.of("files", filePaths));
                questionEntity.setQuestFile(jsonObject.toString());
            }
        }
        questionEntity.setStatus(EStatusQuestion.PENDING.name());
        questionEntity.setCreatedBy(SecurityUtils.getCurrentUser().getUser());
        this.questionRepository.saveAndFlush(questionEntity);
        notificationService.addForSpecificUser(
                new SocketNotificationModel(null, "Bạn có câu hỏi mới!",
                        "", ENotificationCategory.QUESTION,
                        FrontendConfiguration.QUESTION_DETAIL_URL + questionEntity.getId()),
                this.userRepository.getAllIdsByRole(RoleEntity.ADMINISTRATOR));
        return questionEntity;
    }

    @Override
    public List<QuestionEntity> add(List<QuestionModel> model) {
        return null;

    }


    @Override
    public QuestionEntity update(QuestionModel model) {
        QuestionEntity originalQuestion = this.findById(model.getId());

        if (originalQuestion.getCreatedBy().getId().equals(SecurityUtils.getCurrentUserId()) || SecurityUtils.hasRole(RoleEntity.ADMINISTRATOR)) {
            if (originalQuestion.getStatus().equalsIgnoreCase(EStatusQuestion.COMPLETED.name()))
                throw new RuntimeException("Question is already completed");

            if (originalQuestion.getQuestFile() != null) {
                List<Object> originalFile = (this.parseJson(originalQuestion.getQuestFile()).getJSONArray("files").toList());
                originalFile.removeAll(model.getQuestOriginFile());
                originalFile.forEach(o -> fileUploadProvider.deleteFile(o.toString()));
            }

            List<String> uploadedFiles = new ArrayList<String>();
            uploadedFiles.addAll(model.getQuestOriginFile());
            if (model.getQuestFile() != null) {
                if (!model.getQuestFile().get(0).getName().isEmpty()) {
                    model.getQuestFile().forEach(multipartFile -> {
                        try {
                            uploadedFiles.add(fileUploadProvider.uploadFile(UserEntity.FOLDER + originalQuestion.getCreatedBy().getUserName() + QuestionEntity.FOLDER, multipartFile));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
            }
            originalQuestion.setQuestFile(uploadedFiles.isEmpty() ? null : (new JSONObject(Map.of("files", uploadedFiles)).toString()));

            UserEntity userEntity = userService.findById(SecurityUtils.getCurrentUserId());
            originalQuestion.setCreatedBy(userEntity);
            originalQuestion.setTitle(model.getTitle());
            originalQuestion.setQuestContent(model.getQuestContent());
            return this.questionRepository.save(originalQuestion);
        } else
            return null;
    }


    @Override
    public boolean deleteById(Long id) {
        QuestionEntity questionEntity = this.findById(id);
        if (questionEntity.getCreatedBy().getId().equals(SecurityUtils.getCurrentUserId()) || SecurityUtils.hasRole(RoleEntity.ADMINISTRATOR)) {
            if (questionEntity.getQuestFile() != null) {
                new JSONObject(questionEntity.getQuestFile()).getJSONArray("files").toList().forEach(u -> fileUploadProvider.deleteFile(u.toString()));
            }
            questionRepository.deleteById(id);
            return true;
        } else
            return false;
    }

    @Override
    public boolean deleteByIds(List<Long> ids) {
        ids.forEach(id -> this.deleteById(id));
        return true;
    }

    @Override
    public QuestionEntity answerQuestion(Long qid, QuestionResponseModel model) {
        QuestionEntity question = this.findById(qid);

        List<String> uploadedFiles = new ArrayList<String>();
        if (!model.getOldFiles().isEmpty())
            uploadedFiles.addAll(model.getOldFiles());

        if (model.getReplyFile() != null) {
            if (!model.getReplyFile().get(0).getName().isEmpty()) {
                String folder = UserEntity.FOLDER + question.getCreatedBy().getUserName() + QuestionEntity.FOLDER;
                model.getReplyFile().forEach(multipartFile -> {
                    try {
                        uploadedFiles.add(fileUploadProvider.uploadFile(folder, multipartFile));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }

        }
        question.setAnsweredBy(userService.findById(SecurityUtils.getCurrentUserId()));
        question.setReplyContent(model.getReplyContent());
        question.setStatus(EStatusQuestion.COMPLETED.toString());
        question = questionRepository.save(question);
        notifyUser(model.getUrl(), question);
        notificationService.addForSpecificUser(
                new SocketNotificationModel(null,
                        "Admin đã phản hồi lại câu hỏi của bạn!",
                        "", ENotificationCategory.QUESTION,
                        FrontendConfiguration.QUESTION_DETAIL_URL + question.getId()),
                List.of(question.getCreatedBy().getId()));
        return question;
    }


    @Override
    public Page<QuestionEntity> getAllMyQuestion(Pageable page) {
        return questionRepository.getQuestionEntitiesByUIDandCompat(SecurityUtils.getCurrentUserId(), true, page);

    }

    @Override
    public Page<QuestionEntity> getAllMyAnsweredQuestion(Pageable pageable) {
        return questionRepository.findAllQuestionByUID(SecurityUtils.getCurrentUserId(), true, String.valueOf(EStatusQuestion.COMPLETED), pageable);
    }

    @Override
    public Page<QuestionEntity> getAllQuestionByID(Long id, Pageable pageable) {
        return questionRepository.getQuestionEntitiesByUIDandCompat(id, true, pageable);
    }

    @Override
    public Page<QuestionEntity> getAllQuestionByCategory(String category, Pageable pageable) {
        return questionRepository.findQuestionsByCategory(category, pageable);
    }

    @Override
    public Page<QuestionEntity> getAllQuestionAnsweredByID(Long id, Pageable pageable) {
        return questionRepository.findAllQuestionByUID(id, true, String.valueOf(EStatusQuestion.COMPLETED), pageable);
    }

    @Override
    public List<Long> getAllAskedUser() {
        return questionRepository.getAllAskedUserIDByStatus().stream().map(UserEntity::getId).collect(Collectors.toList());
    }

    @Override
    public Page<QuestionEntity> userGetAllQuestionByCategory(String name, Pageable pageable) {
        return this.questionRepository.findQuestionsByCategoryAndUserId(name, SecurityUtils.getCurrentUserId(), pageable);
    }

    @Override
    public QuestionEntity getQuestionByIdAndUserId(Long id, Long currentUserId) {
        return this.questionRepository.findByIdAndCreatedById(id, currentUserId).orElseThrow(() -> new RuntimeException("Question not found id: " + id));
    }

    public JSONObject parseJson(String json) {
        return new JSONObject(json);
    }

    private void notifyUser(String url, QuestionEntity question) {
        new Thread("Send Notify Mail") {
            @Override
            public void run() {
                Map<String, Object> context = new HashMap<>();
                context.put("url", url);
                context.put("question", question);
                try {
                    mailService.sendMail("QuestionNotifyMailTemplate.html", question.getCreatedBy().getEmail(), "Question Answered", context);
                } catch (MessagingException e) {
                    throw new RuntimeException(e);
                }
            }
        }.start();
    }
}