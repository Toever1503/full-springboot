package com.services.serviceimps;

import com.dtos.EStatusQuestion;
import com.entities.QuestionEntity;
import com.entities.UserEntity;
import com.models.QuestionModel;
import com.repositories.IQuestionRepository;
import com.services.IQuestionService;
import com.services.IUserService;
import com.utils.FileUploadProvider;
import com.utils.SecurityUtils;
import org.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class QuestionServiceImp implements IQuestionService {
    private final IQuestionRepository questionRepository;
    private final IUserService userService;
    private final FileUploadProvider fileUploadProvider;

    public QuestionServiceImp(IQuestionRepository questionRepository, IUserService userService, FileUploadProvider fileUploadProvider) {
        this.questionRepository = questionRepository;
        this.userService = userService;
        this.fileUploadProvider = fileUploadProvider;
    }

    @Override
    public List<QuestionEntity> findAll() {
        return null;
    }

    @Override
    public Page<QuestionEntity> findAll(Pageable page) {
        return this.questionRepository.findAll(page);
    }

    @Override
    public QuestionEntity findById(Long id) {
        return questionRepository.findById(id).orElse(null);
    }

    @Override
    public QuestionEntity add(QuestionModel model)  {
        QuestionEntity questionEntity = QuestionModel.toEntity(model);
        if(!model.getQuestFile().get(0).getOriginalFilename().equals("")){
            if(model.getQuestFile().size() >=3) {
                throw new RuntimeException("File upload limit is 3");
            }
            List<String> filePaths =new ArrayList<>();
            for(MultipartFile file: model.getQuestFile()){
                try {
                    filePaths.add(fileUploadProvider.uploadFile(SecurityUtils.getCurrentUsername(),file));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            JSONObject jsonObject = new JSONObject(Map.of("files", filePaths));
            questionEntity.setQuestFile(jsonObject.toString());
        }

        questionEntity.setStatus(EStatusQuestion.PENDING.name());
        Long userId = SecurityUtils.getCurrentUserId();
        UserEntity userEntity = userService.findById(userId);
        questionEntity.setCreatedBy(userEntity);
        return this.questionRepository.save(questionEntity);
    }

    @Override
    public List<QuestionEntity> add(List<QuestionModel> model) {
        return null;
    }

    @Override
    public QuestionEntity update(QuestionModel model) {
        QuestionEntity originalQuestion = this.findById(model.getId());
        QuestionEntity questionEntity = QuestionModel.toEntity(model);
        if(originalQuestion.getStatus().equalsIgnoreCase(EStatusQuestion.COMPLETED.name())) throw new RuntimeException("Question is already completed");
            if(!model.getQuestFile().get(0).isEmpty()){
                if(model.getQuestOriginFile() != null) { // model old file 1-3
                    model.getQuestOriginFile().stream().forEach(u -> fileUploadProvider.deleteFile(u)); // delete old file, 1-3
                }

                List<String> filePaths =new ArrayList<>();
                // in database file 1- 5
                // old file 1-3
                // we have to delete file 4,5
                for(MultipartFile file: model.getQuestFile()){
                    try {
                        filePaths.add(fileUploadProvider.uploadFile(SecurityUtils.getCurrentUsername(),file));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                JSONObject jsonObject = new JSONObject(Map.of("files", filePaths));
                questionEntity.setQuestFile(jsonObject.toString());
            }else {
                questionEntity.setQuestFile(originalQuestion.getQuestFile());
            }


        questionEntity.setStatus(EStatusQuestion.PENDING.name());
        Long userId = SecurityUtils.getCurrentUserId();
        UserEntity userEntity = userService.findById(userId);
        questionEntity.setCreatedBy(userEntity);
        questionEntity.setTitle(model.getTitle().equals("") ? originalQuestion.getTitle() : model.getTitle());
        questionEntity.setQuestContent(model.getQuestContent().equals("") ? originalQuestion.getQuestContent() : model.getQuestContent());
        return this.questionRepository.save(questionEntity);
    }

    @Override
    public boolean deleteById(Long id) {
        QuestionEntity questionEntity = this.findById(id);
        if(questionEntity.getQuestFile() != null) {
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
}
