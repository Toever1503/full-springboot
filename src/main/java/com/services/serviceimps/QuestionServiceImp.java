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

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class QuestionServiceImp implements IQuestionService {
    final
    IQuestionRepository questionRepository;
    final IUserRepository userRepository;

    final
    FileUploadProvider fileUploadProvider;

    final MailService mailService;

    public QuestionServiceImp(IQuestionRepository questionRepository, IUserRepository userRepository, FileUploadProvider fileUploadProvider, MailService mailService) {
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
        this.fileUploadProvider = fileUploadProvider;
        this.mailService = mailService;
    }

    @Override
    public List<QuestionEntity> findAll() {
        return null;
    }

    @Override
    public Page<QuestionEntity> findAll(Pageable page) {
        return questionRepository.findAllByCompatible(true, page);
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
        return null;
    }

    @Override
    public boolean deleteById(Long id) {
        return false;
    }

    @Override
    public boolean deleteByIds(List<Long> id) {
        return false;
    }

    @Override
    public QuestionResponseDto answerQuestion(Long qid, QuestionResponseModel model) {
        QuestionEntity question = this.findById(qid);
        List<Object> originalFile;
        if(question.getReplyFile()!=null){
            originalFile = (parseJson(question.getReplyFile()).getJSONArray("files").toList());
            originalFile.removeAll(model.getOldFiles());
            originalFile.forEach(o -> fileUploadProvider.deleteFile(o.toString()));
        }else {
            originalFile = new ArrayList<>();
        }
        uploadFile(question,model.getReplyFile(),model.getOldFiles());
        question.setAnsweredBy(userRepository.findById(SecurityUtils.getCurrentUserId()).orElseThrow(() -> new RuntimeException("User not found")));
        question.setReplyContent(model.getReplyContent());

        question.setStatus(StatusQuestion.COMPLETED.toString());
        question = questionRepository.save(question);
        notifyUser(model.getUrl(), question);
        return QuestionResponseDto.builder().id(question.getId()).replyContent(question.getReplyContent()).userReply(question.getAnsweredBy().getId()).updatedDate(question.getUpdatedDate()).replyFiles(model.getReplyFile().stream().map(multipartFile -> String.valueOf(multipartFile.getOriginalFilename())).collect(Collectors.toList())).status(StatusQuestion.COMPLETED).build();
    }

    @Override
    public Page<QuestionEntity> getAllMyQuestion(Pageable page) {
        return questionRepository.getQuestionEntitiesByUIDandCompat(SecurityUtils.getCurrentUserId(), true, page);

    }

    @Override
    public Page<QuestionEntity> getAllMyAnsweredQuestion(Pageable pageable) {
        return questionRepository.findAllQuestionByUID(SecurityUtils.getCurrentUserId(),true, String.valueOf(StatusQuestion.COMPLETED), pageable);
    }

    @Override
    public Page<QuestionEntity> getAllQuestionByID(Long id, Pageable pageable) {
        return questionRepository.getQuestionEntitiesByUIDandCompat(id, true, pageable);
//        return questionEntities.getContent().stream().map(QuestionDto::toDto).collect(Collectors.toList());
    }

    @Override
    public Page<QuestionEntity> getAllQuestionAnsweredByID(Long id, Pageable pageable) {
        return questionRepository.findAllQuestionByUID(id, true,String.valueOf(StatusQuestion.COMPLETED),pageable);
    }

    @Override
    public List<Long> getAllAskedUser() {
        return questionRepository.getAllAskedUserIDByStatus().stream().map(UserEntity::getId).collect(Collectors.toList());
    }


    private void uploadFile(QuestionEntity entity, List<MultipartFile> multipartFiles, List<String> oldFiles){
        if (!multipartFiles.get(0).getName().isEmpty()) {
            String folder = UserEntity.FOLDER + entity.getCreatedBy().getUserName() + QuestionEntity.FOLDER;
            List<String> uploadedFiles = new ArrayList<String>();
            if(!oldFiles.isEmpty()){
                uploadedFiles.addAll(oldFiles);
            }
            multipartFiles.forEach(multipartFile -> {
                try {
                    uploadedFiles.add(fileUploadProvider.uploadFile(folder, multipartFile));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            JSONObject jsonObject = new JSONObject(Map.of("files", uploadedFiles));
            entity.setReplyFile(jsonObject.toString());
        }

    }
    private JSONObject parseJson(String json){
        return new JSONObject(json);
    }

    private void notifyUser(String url, QuestionEntity question){
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
