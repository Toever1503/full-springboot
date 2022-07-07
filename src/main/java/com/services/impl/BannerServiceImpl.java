package com.services.impl;

import com.entities.BannerEntity;
import com.entities.RoleEntity;
import com.entities.UserEntity;
import com.models.BannerModel;
import com.repositories.IBannerRepository;
import com.services.IBannerService;
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

import static com.dtos.QuestionDto.parseJson;

@Service
public class BannerServiceImpl implements IBannerService {
    private final IBannerRepository bannerRepository;
    private final FileUploadProvider fileUploadProvider;

    public BannerServiceImpl(IBannerRepository bannerRepository, FileUploadProvider fileUploadProvider) {
        this.bannerRepository = bannerRepository;
        this.fileUploadProvider = fileUploadProvider;
    }

    @Override
    public List<BannerEntity> findAll() {
        return null;
    }

    @Override
    public Page<BannerEntity> findAll(Pageable page) {
        return this.bannerRepository.findAll(page);
    }

    @Override
    public List<BannerEntity> findAll(Specification<BannerEntity> specs) {
        return null;
    }

    @Override
    public Page<BannerEntity> filter(Pageable page, Specification<BannerEntity> specs) {
        return null;
    }

    @Override
    public BannerEntity findById(Long id) {
        return this.bannerRepository.findById(id).orElseThrow(() -> new RuntimeException("Banner Not found: " + id));
    }

    @Override
    public BannerEntity add(BannerModel model) {
        final String folder = "user/" + SecurityUtils.getCurrentUsername() + "/banner/";
        BannerEntity bannerEntity = BannerModel.toEntity(model);

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
            bannerEntity.setAttachFiles(jsonObject.toString());
        }
        bannerEntity.setCreatedBy(SecurityUtils.getCurrentUser().getUser());

        return this.bannerRepository.save(bannerEntity);
    }

    @Override
    public List<BannerEntity> add(List<BannerModel> model) {
        return null;
    }

    @Override
    public BannerEntity update(BannerModel model) {
        BannerEntity originalBanner = this.findById(model.getId());
        final String folder = UserEntity.FOLDER + originalBanner.getCreatedBy().getUserName() + "/banner/";

        //delete file into s3
        List<Object> originalFile;
        if (originalBanner.getAttachFiles() != null) {
            originalFile = (parseJson(originalBanner.getAttachFiles()).getJSONArray("files").toList());
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
        originalBanner.setAttachFiles(uploadedFiles.isEmpty() ? null : (new JSONObject(Map.of("files", uploadedFiles)).toString()));

        originalBanner.setName(model.getName());
        originalBanner.setCreatedBy(SecurityUtils.getCurrentUser().getUser());
        originalBanner.setIsEdit(true);
        originalBanner.setStatus(model.getStatus());

        return this.bannerRepository.save(originalBanner);
    }

    @Override
    public boolean deleteById(Long id) {
        BannerEntity bannerEntity = this.findById(id);
        if(bannerEntity.getCreatedBy().getId().equals(SecurityUtils.getCurrentUserId()) || SecurityUtils.hasRole(RoleEntity.ADMINISTRATOR)){
            if (bannerEntity.getAttachFiles() != null) {
                new JSONObject(bannerEntity.getAttachFiles()).getJSONArray("files").toList().forEach(u -> fileUploadProvider.deleteFile(u.toString()));
            }
            this.bannerRepository.deleteById(id);
            return true;
        }
        else
            return false;
    }

    @Override
    public BannerEntity updateStatus(Long id, String status) {
        BannerEntity originalBanner = this.findById(id);
        originalBanner.setStatus(status);
        return this.bannerRepository.save(originalBanner);
    }

    @Override
    public boolean deleteByIds(List<Long> ids) {
        ids.forEach(id -> this.deleteById(id));
        return true;
    }
}
