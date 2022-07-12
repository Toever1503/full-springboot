package com.services.impl;

import com.entities.OptionsEntity;
import com.models.OptionsModel;
import com.repositories.ICategoryRepository;
import com.repositories.IOptionsRepository;
import com.services.IOptionsService;
import com.utils.FileUploadProvider;
import org.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class OptionsServiceImpl implements IOptionsService {
    final private IOptionsRepository optionsRepository;
    private final FileUploadProvider fileUploadProvider;
    private final ICategoryRepository categoryRepository;

    public OptionsServiceImpl(IOptionsRepository optionsRepository, FileUploadProvider fileUploadProvider, ICategoryRepository categoryRepository) {
        this.optionsRepository = optionsRepository;
        this.fileUploadProvider = fileUploadProvider;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<OptionsEntity> findAll() {
        return null;
    }

    @Override
    public Page<OptionsEntity> findAll(Pageable page) {
        return null;
    }

    @Override
    public List<OptionsEntity> findAll(Specification<OptionsEntity> specs) {
        return null;
    }

    @Override
    public Page<OptionsEntity> filter(Pageable page, Specification<OptionsEntity> specs) {
        return null;
    }

    @Override
    public OptionsEntity findById(Long id) {
        return null;
    }

    @Override
    public OptionsEntity add(OptionsModel model) {
        return null;
    }

    @Override
    public List<OptionsEntity> add(List<OptionsModel> model) {
        return null;
    }

    @Override
    public OptionsEntity update(OptionsModel model) {
        return null;
    }

    @Override
    public boolean deleteById(Long id) {
        return false;
    }

    @Override
    public boolean deleteByIds(List<Long> ids) {
        return false;
    }


    @Override
    public OptionsEntity addOptions(OptionsModel model, HttpServletRequest request) {
        String optionKey =  request.getParameter("optionKey");
        this.optionsRepository.findByOptionKey(optionKey).ifPresent(optionsEntity -> {
            throw new RuntimeException("Option key already exists");
        });

        if(model == null) throw new RuntimeException("Options model is null");
        OptionsEntity optionsEntity = OptionsEntity.builder()
                .optionKey(model.getOptionKey())
                .build();

        String json = model.getOptionValue();
        JSONObject root = new JSONObject(json);

        List<Object> slides = root.getJSONArray("slides").toList();
        List<Object> categories = root.getJSONArray("categories").toList();
        List<Object> recommendProductFilter = root.getJSONArray("recommendProductFilter").toList();

        CompletableFuture<Void> imgUploadFuture = null;
        StringBuilder valueNew = new StringBuilder();

        // Upload image slides
        for (Object slide : slides) {
            Map<String,Object> slideMap = (Map<String, Object>) slide;
            if(slideMap.get("imageParameter") != null) {
                fileUploadProvider.deleteFile(slideMap.get("imageUrl").toString());
                try {
                    Part filePart = request.getPart(slideMap.get("imageParameter").toString());
                    imgUploadFuture = fileUploadProvider.asyncUpload1File("slides", filePart).thenAccept(url -> slideMap.put("imageUrl", url));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ServletException e) {
                    throw new RuntimeException(e);
                }
            }
            slideMap.remove("imageParameter");
        }
        valueNew.append(slides);

        // Upload image banner 1
        Object banner1 = root.getJSONObject("imageBanner1");
        Map<String, Object> banner1Map = (Map<String, Object>) banner1;
        if(banner1Map.get("imageParameter") != null) {
            fileUploadProvider.deleteFile(banner1Map.get("imageUrl").toString());
            try {
                Part filePart = request.getPart(banner1Map.get("imageParameter").toString());
                imgUploadFuture = fileUploadProvider.asyncUpload1File("banner1", filePart).thenAccept(url -> banner1Map.put("imageUrl", url));
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ServletException e) {
                throw new RuntimeException(e);
            }
        }
        banner1Map.remove("imageParameter");

        // Upload image banner 2
        Object banner2 = root.getJSONObject("imageBanner2");
        Map<String, Object> banner2Map = (Map<String, Object>) banner2;
        if(banner2Map.get("imageParameter") != null) {
            fileUploadProvider.deleteFile(banner2Map.get("imageUrl").toString());
            try {
                Part filePart = request.getPart(banner2Map.get("imageParameter").toString());
                imgUploadFuture = fileUploadProvider.asyncUpload1File("banner2", filePart).thenAccept(url -> banner2Map.put("imageUrl", url));
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ServletException e) {
                throw new RuntimeException(e);
            }
        }
        banner2Map.remove("imageParameter");

        //check id category into database
        List<Long> listCatId = categories.stream().map(id -> Long.parseLong(id.toString())).collect(java.util.stream.Collectors.toList());
        this.categoryRepository.findAllById(listCatId);

        if (imgUploadFuture != null) {
            try {
                imgUploadFuture.get();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }

        Map<String, Object> finalRs = new HashMap<>();
        finalRs.put("slides", slides);
        finalRs.put("categories", categories);
        finalRs.put("recommendProductFilter", recommendProductFilter);
        finalRs.put("imageBanner1", banner1Map);
        finalRs.put("imageBanner2", banner2Map);

        optionsEntity.setOptionValue(new JSONObject(finalRs).toString());

        return this.optionsRepository.save(optionsEntity);
    }
}
