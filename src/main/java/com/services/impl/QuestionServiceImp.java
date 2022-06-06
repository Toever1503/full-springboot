package com.services.impl;

import com.dtos.EStatusQuestion;
import com.entities.QuestionEntity;
import com.entities.UserEntity;
import com.models.QuestionModel;
import com.models.QuestionResponseModel;
import com.repositories.IQuestionRepository;
import com.services.IQuestionService;
import com.services.IUserService;
import com.services.MailService;
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
    final MailService mailService;

    public QuestionServiceImp(IQuestionRepository questionRepository, IUserService userService, FileUploadProvider fileUploadProvider, MailService mailService) {
        this.questionRepository = questionRepository;
        this.userService = userService;
        this.fileUploadProvider = fileUploadProvider;
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
    public Page<QuestionEntity> filter(Pageable page, Specification<QuestionEntity> specs) {
        return null;
    }

    @Override
    public QuestionEntity findById(Long id) {
        return questionRepository.findById(id).orElse(null);
    }

    @Override
    public QuestionEntity add(QuestionModel model) {
        QuestionEntity questionEntity = QuestionModel.toEntity(model);
        if (!model.getQuestFile().get(0).isEmpty()) {
            List<String> filePaths = new ArrayList<>();
            for (MultipartFile file : model.getQuestFile()) {
                try {
                    filePaths.add(fileUploadProvider.uploadFile(UserEntity.FOLDER + SecurityUtils.getCurrentUsername() + QuestionEntity.FOLDER, file));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            JSONObject jsonObject = new JSONObject(Map.of("files", filePaths));
            questionEntity.setQuestFile(jsonObject.toString());
        }
        questionEntity.setStatus(EStatusQuestion.PENDING.name());
        questionEntity.setCreatedBy(SecurityUtils.getCurrentUser().getUser());
        return this.questionRepository.save(questionEntity);
    }

    @Override
    public List<QuestionEntity> add(List<QuestionModel> model) {
        return null;

    }

    @Override
    public QuestionEntity update(QuestionModel model) {
        QuestionEntity originalQuestion = this.findById(model.getId());
        if (originalQuestion.getStatus().equalsIgnoreCase(EStatusQuestion.COMPLETED.name()))
            throw new RuntimeException("Question is already completed");

        if (originalQuestion.getQuestFile() != null) {
            List<Object> originalFile = (parseJson(originalQuestion.getQuestFile()).getJSONArray("files").toList());
            originalFile.removeAll(model.getQuestOriginFile());
            originalFile.forEach(o -> fileUploadProvider.deleteFile(o.toString()));
        }
        List<String> uploadedFiles = new ArrayList<String>();
        if (!model.getQuestFile().isEmpty())
            uploadedFiles.addAll(model.getQuestOriginFile());

        if (!model.getQuestFile().get(0).getName().isEmpty()) {
            model.getQuestFile().forEach(multipartFile -> {
                try {
                    uploadedFiles.add(fileUploadProvider.uploadFile(UserEntity.FOLDER + originalQuestion.getCreatedBy().getUserName() + QuestionEntity.FOLDER, multipartFile));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        originalQuestion.setQuestFile(uploadedFiles.isEmpty() ? null : (new JSONObject(Map.of("files", uploadedFiles)).toString()));

        UserEntity userEntity = userService.findById(SecurityUtils.getCurrentUserId());
        originalQuestion.setCreatedBy(userEntity);
        originalQuestion.setTitle(model.getTitle());
        originalQuestion.setQuestContent(model.getQuestContent());
        return this.questionRepository.save(originalQuestion);

    }

    @Override
    public boolean deleteById(Long id) {
        QuestionEntity questionEntity = this.findById(id);
        if (questionEntity.getQuestFile() != null) {
            new JSONObject(questionEntity.getQuestFile()).getJSONArray("files").toList().forEach(u -> fileUploadProvider.deleteFile(u.toString()));
        }
        if (questionEntity.getReplyFile() != null) {
            new JSONObject(questionEntity.getReplyFile()).getJSONArray("files").toList().forEach(u -> fileUploadProvider.deleteFile(u.toString()));
        }
        questionRepository.deleteById(id);
        return true;

    }

    @Override
    public boolean deleteByIds(List<Long> ids) {
        ids.forEach(id -> this.deleteById(id));
        return true;
    }


    @Override
    public QuestionEntity answerQuestion(Long qid, QuestionResponseModel model) {
        QuestionEntity question = this.findById(qid);

        if (question.getReplyFile() != null) {
            List<Object> originalFile = (parseJson(question.getReplyFile()).getJSONArray("files").toList());
            originalFile.removeAll(model.getOldFiles());
            originalFile.forEach(o -> fileUploadProvider.deleteFile(o.toString()));
        }
        List<String> uploadedFiles = new ArrayList<String>();
        if (!model.getOldFiles().isEmpty())
            uploadedFiles.addAll(model.getOldFiles());

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
        question.setReplyFile(uploadedFiles.isEmpty() ? null : (new JSONObject(Map.of("files", uploadedFiles)).toString()));

        question.setAnsweredBy(userService.findById(SecurityUtils.getCurrentUserId()));
        question.setReplyContent(model.getReplyContent());
        question.setStatus(EStatusQuestion.COMPLETED.toString());
        question = questionRepository.save(question);
        notifyUser(model.getUrl(), question);
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

    private JSONObject parseJson(String json) {
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
