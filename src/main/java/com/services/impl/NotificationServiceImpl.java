package com.services.impl;

import com.entities.NotificationEntity;
import com.entities.NotificationUser;
import com.entities.UserEntity;
import com.models.NotificationModel;
import com.repositories.INotificationRepository;
import com.repositories.INotificationUserRepository;
import com.repositories.IUserRepository;
import com.services.INotificationService;
import com.services.IUserService;
import com.utils.FileUploadProvider;
import com.utils.SecurityUtils;
import org.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements INotificationService {
    private final INotificationRepository notificationRepository;
    private final FileUploadProvider fileUploadProvider;
    private final IUserService userService;
    private final INotificationUserRepository notificationUserRepository;
    private final IUserRepository userRepository;

    public NotificationServiceImpl(INotificationRepository notificationRepository, FileUploadProvider fileUploadProvider, IUserService userService, INotificationUserRepository notificationUserRepository, IUserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.fileUploadProvider = fileUploadProvider;
        this.userService = userService;
        this.notificationUserRepository = notificationUserRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<NotificationEntity> findAll() {
        return this.notificationRepository.findAll();
    }

    @Override
    public Page<NotificationEntity> findAll(Pageable page) {
        return null;
    }

    @Override
    public Page<NotificationEntity> filter(Pageable page, Specification<NotificationEntity> specs) {
        return null;
    }

    @Override
    public NotificationEntity findById(Long id) {
        return notificationRepository.findById(id).orElse(null);
    }

    @Override
    public NotificationEntity add(NotificationModel model) {
        NotificationEntity notificationEntity = NotificationModel.toEntity(model);

        if (!model.getAttachFiles().get(0).getOriginalFilename().equals("")) {
            List<String> filePaths = new ArrayList<>();
            for (MultipartFile file : model.getAttachFiles()) {
                try {
                    filePaths.add(fileUploadProvider.uploadFile("user/" + SecurityUtils.getCurrentUsername() + "/notification/", file));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            JSONObject jsonObject = new JSONObject(Map.of("files", filePaths));
            notificationEntity.setAttachFiles(jsonObject.toString());
        }

        if (!model.getImage().getOriginalFilename().equals("")) {
            String filePath = null;
            try{
                filePath = fileUploadProvider.uploadFile("user" + SecurityUtils.getCurrentUsername() + "/notification/", model.getImage());
                notificationEntity.setImage(filePath);
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        UserEntity userEntity = userService.findById(SecurityUtils.getCurrentUserId());
        notificationEntity.setCreatedBy(userEntity);
        notificationEntity = this.notificationRepository.save(notificationEntity);
        final long notificationId = notificationEntity.getId();
        this.notificationUserRepository.saveAll(
                this.userRepository.getAllId().stream().map(id -> NotificationUser.builder()
                        .isRead(false)
                        .notificationId(notificationId)
                        .userId(id)
                        .build()).collect(Collectors.toList())
        );

        return notificationEntity;
    }

    @Override
    public List<NotificationEntity> add(List<NotificationModel> model) {
        return null;
    }

    @Override
    public NotificationEntity update(NotificationModel model) {
        return null;
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

        notificationRepository.deleteById(id);
        return true;
    }

    @Override
    public boolean deleteByIds(List<Long> ids) {
        return false;
    }
}
