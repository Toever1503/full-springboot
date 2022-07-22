package com.services.impl;

import com.dtos.ENotificationCategory;
import com.dtos.ENotificationStatus;
import com.dtos.NotificationDto;
import com.entities.NotificationEntity;
import com.entities.NotificationEntity_;
import com.entities.NotificationUser;
import com.entities.UserEntity;
import com.models.NotificationModel;
import com.models.SocketNotificationModel;
import com.models.specifications.NotificationSpecification;
import com.repositories.INotificationRepository;
import com.repositories.INotificationUserRepository;
import com.repositories.IUserRepository;
import com.services.INotificationService;
import com.services.ISocketService;
import com.services.IUserService;
import com.utils.FileUploadProvider;
import com.utils.SecurityUtils;
import org.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.dtos.QuestionDto.parseJson;

@Service
public class NotificationServiceImpl implements INotificationService {
    private final INotificationRepository notificationRepository;
    private final FileUploadProvider fileUploadProvider;
    private final IUserService userService;
    private final INotificationUserRepository notificationUserRepository;
    private final IUserRepository userRepository;
    private final ISocketService socketService;

    public NotificationServiceImpl(INotificationRepository notificationRepository,
                                   FileUploadProvider fileUploadProvider,
                                   IUserService userService,
                                   INotificationUserRepository notificationUserRepository,
                                   IUserRepository userRepository,
                                   ISocketService socketService) {
        this.notificationRepository = notificationRepository;
        this.fileUploadProvider = fileUploadProvider;
        this.userService = userService;
        this.notificationUserRepository = notificationUserRepository;
        this.userRepository = userRepository;
        this.socketService = socketService;
    }

    @Override
    public List<NotificationEntity> findAll() {
        return this.notificationRepository.findAll();
    }

    @Override
    public Page<NotificationEntity> findAll(Pageable page) {
        return this.notificationRepository.findAll(Specification.where(
                (root, query, cb) -> cb.equal(root.get(NotificationEntity_.isJustNotice), false)
        ), page);
    }

    @Override
    public List<NotificationEntity> findAll(Specification<NotificationEntity> specs) {
        return null;
    }

    @Override
    public Page<NotificationEntity> filter(Pageable page, Specification<NotificationEntity> specs) {
        return notificationRepository.findAll(specs, page);
    }

    @Override
    public NotificationEntity findById(Long id) {
        return this.notificationRepository.findById(id).orElseThrow(() -> new RuntimeException("không tìm thấy thông báo, id: " + id));
    }

    @Override
    public NotificationEntity add(NotificationModel model) {
        NotificationEntity notificationEntity = NotificationModel.toEntity(model);
        final String folder = "user/" + SecurityUtils.getCurrentUsername() + "/notification/";

        if (model.getAttachFiles() != null) { // check if model has attached file
            List<String> filePaths = new ArrayList<>();
            for (MultipartFile file : model.getAttachFiles()) {
                try {
                    filePaths.add(fileUploadProvider.uploadFile(folder, file));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            JSONObject jsonObject = new JSONObject(Map.of("files", filePaths));
            notificationEntity.setAttachFiles(jsonObject.toString());
        }

        if (model.getImage() != null) {//Check if notification avatar is empty or not
            String filePath;
            try {
                filePath = fileUploadProvider.uploadFile(folder, model.getImage());
                notificationEntity.setImage(filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        UserEntity userEntity = userService.findById(SecurityUtils.getCurrentUserId());
        notificationEntity.setCreatedBy(userEntity);
        if (model.getUrl() != null) {
            notificationEntity.setUrl(model.getUrl());
        }
        notificationEntity = this.notificationRepository.save(notificationEntity);
        notificationEntity.setIsJustNotice(false);
        this.saveUserNotification(notificationEntity.getId(), this.userRepository.getAllId());
        this.socketService.sendNotificationForAllUser(SocketNotificationModel.toModel(notificationEntity));
        return notificationEntity;
    }

    public void saveUserNotification(Long noId, List<Long> userIds) {
        this.notificationUserRepository.saveAll(
                userIds.stream().map(uId -> NotificationUser.builder()
                        .isRead(false)
                        .notificationId(noId)
                        .userId(uId)
                        .build()).collect(Collectors.toList())
        );
    }

    @Override
    public List<NotificationEntity> add(List<NotificationModel> model) {
        return null;
    }

    @Override
    public NotificationEntity update(NotificationModel model) {
        NotificationEntity originNotificationEntity = this.notificationRepository.findById(model.getId()).orElseThrow(() -> new RuntimeException("thông báo với id: " + model.getId() + " không có"));
        final String folder = UserEntity.FOLDER + originNotificationEntity.getCreatedBy().getUserName() + "/" + NotificationEntity.FOLDER;

        // giới hạn số lần + thời gian sửa file
        if ((originNotificationEntity.getCountEdit() < NotificationEntity.limitEditCount)) {
            long difference = (new Date().getTime() - originNotificationEntity.getUpdatedDate().getTime()) / 60000;

            if (difference > NotificationEntity.limitEditMin) {
                throw new RuntimeException("bạn chỉ có thể chỉnh sửa thông báo trong " + NotificationEntity.limitEditMin + " phút");
            } else {
                originNotificationEntity.setCountEdit(originNotificationEntity.getCountEdit() + 1);

                //delete file into s3
                List<Object> originalFile;
                if (originNotificationEntity.getAttachFiles() != null) {
                    originalFile = (parseJson(originNotificationEntity.getAttachFiles()).getJSONArray("files").toList());
                    originalFile.removeAll(model.getAttachFilesOrigin());
                    originalFile.forEach(o -> fileUploadProvider.deleteFile(o.toString()));
                }

                //add old file to uploadFiles
                List<String> uploadedFiles = new ArrayList<>();
                if (!model.getAttachFilesOrigin().isEmpty())
                    uploadedFiles.addAll(model.getAttachFilesOrigin());

                //upload new file to uploadFiles and save to database
                if (model.getAttachFiles() != null) {
                    for (MultipartFile file : model.getAttachFiles()) {
                        try {
                            uploadedFiles.add(fileUploadProvider.uploadFile(folder, file));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                originNotificationEntity.setAttachFiles(uploadedFiles.isEmpty() ? null : (new JSONObject(Map.of("files", uploadedFiles)).toString()));

                // edit image into notification
                if (model.getImage() != null) {
                    String filePath;
                    try {
                        filePath = fileUploadProvider.uploadFile(folder, model.getImage());
                        fileUploadProvider.deleteFile(originNotificationEntity.getImage());
                        originNotificationEntity.setImage(filePath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                originNotificationEntity.setCategory(model.getCategory().name());
                originNotificationEntity.setTitle(model.getTitle());
                originNotificationEntity.setContent(model.getContent());
                originNotificationEntity.setContentExcerpt(model.getContentExcerpt());
                originNotificationEntity.setIsEdit(true);

                originNotificationEntity.setStatus(model.getStatus().name());
                originNotificationEntity.setFutureDate(model.getFutureDate() == null ? null : new Date(model.getFutureDate().getTime()));

                UserEntity userEntity = userService.findById(SecurityUtils.getCurrentUserId());
                originNotificationEntity.setCreatedBy(userEntity);
                return this.notificationRepository.save(originNotificationEntity);
            }
        } else {
            throw new RuntimeException("sửa thông báo thất bại, bạn chỉ có thể sửa thông báo " + NotificationEntity.limitEditCount + " lần");
        }
    }

    @Override
    public boolean deleteById(Long id) {
        NotificationEntity notificationEntity = this.findById(id);
        if (notificationEntity.getAttachFiles() != null) {
            new JSONObject(notificationEntity.getAttachFiles()).getJSONArray("files").toList().forEach(u -> fileUploadProvider.deleteFile(u.toString()));
        }

        if (notificationEntity.getImage() != null) {
            fileUploadProvider.deleteFile(notificationEntity.getImage());
        }
        this.notificationUserRepository.deleteAllByNotificationId(id);
        this.notificationRepository.deleteById(id);
        return true;
    }

    @Override
    public boolean deleteByIds(List<Long> ids) {
        return false;
    }

    @Override
    public Page<NotificationDto> userGetAllNotifications(Pageable page) {
        return this.notificationRepository.userGetAllNotifications(SecurityUtils.getCurrentUserId(), "POSTED", page);
    }

    @Override
    public void addForSpecificUser(SocketNotificationModel model, List<Long> userId) {
        new Thread(() -> {
            NotificationEntity entity = SocketNotificationModel.toEntity(model);
            entity.setStatus(ENotificationStatus.POSTED.name());
            Object auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null)
                entity.setCreatedBy(SecurityUtils.getCurrentUser().getUser());
            else
                entity.setCreatedBy(this.userService.findById(6l));
            this.notificationRepository.saveAndFlush(entity);
            entity.setUrl(model.getUrl());
            this.notificationRepository.saveAndFlush(entity);

            this.saveUserNotification(entity.getId(), userId);
            this.socketService.sendNotificationForSpecificUser(SocketNotificationModel.toModel(entity), userId);
        }).run();
    }


    @Override
    public void addSocketNotificationForAll(SocketNotificationModel model) {
        new Thread(() -> {
            NotificationEntity entity = SocketNotificationModel.toEntity(model);
            entity.setStatus(ENotificationStatus.POSTED.name());
            entity.setCreatedBy(SecurityUtils.getCurrentUser().getUser());
            entity = this.notificationRepository.save(entity);
            this.saveUserNotification(entity.getId(), this.userRepository.getAllId());
            this.socketService.sendNotificationForAllUser(SocketNotificationModel.toModel(entity));
        }).run();
    }

    @Override
    public boolean increaseView(long id) {
        NotificationUser notificationUser = this.notificationUserRepository.findByUserIdAndNotificationId(SecurityUtils.getCurrentUserId(), id).orElseThrow(() -> new RuntimeException("không tìm thấy thông báo với id người dùng: " + id));
        NotificationEntity entity = this.findById(id);
        entity.setViewed(entity.getViewed() == null ? 0 : entity.getViewed() + 1);
        notificationUser.setIsRead(true);
        this.notificationUserRepository.save(notificationUser);
        this.notificationRepository.save(entity);
        return true;
    }

    @Override
    public boolean setAllRead() {
        this.notificationUserRepository.setReadAll(SecurityUtils.getCurrentUserId());
        return true;
    }

}
