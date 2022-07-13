package com.services.impl;

import com.entities.CategoryEntity;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

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
    public OptionsEntity settingUpdateHomePage(OptionsModel model, HttpServletRequest request) {
        String optionKey =  request.getParameter("optionKey");
        OptionsEntity optionsCheck = this.optionsRepository.findByOptionKey(optionKey).orElse(null);

        if(model == null) throw new RuntimeException("Options model is null");
        OptionsEntity optionsEntity = OptionsEntity.builder()
                .optionKey(model.getOptionKey())
                .build();

        String json = model.getOptionValue();
        JSONObject root = new JSONObject(json);

        List<Object> slides = root.getJSONArray("slides").toList();
        List<Object> categories = root.getJSONArray("categories").toList();
        List<Object> recommendProductFilter = root.getJSONArray("recommendProductFilter").toList();

        List<CompletableFuture<Void>> imgUploadFutures = new ArrayList<>();

        // Upload image slides
        for (Object slide : slides) {
            Map<String,Object> slideMap = (Map<String, Object>) slide;
            if(slideMap.get("imageParameter") != null) {
                try {
                    fileUploadProvider.deleteFile(slideMap.get("imageUrl").toString());
                    Part filePart = request.getPart(slideMap.get("imageParameter").toString());
                    imgUploadFutures.add( fileUploadProvider.asyncUpload1File("slides/", filePart).thenAccept(url -> {
                        slideMap.put("imageUrl", url);
                    }));

                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ServletException e) {
                    throw new RuntimeException(e);
                }
            }
            slideMap.remove("imageParameter");
        }

        // Upload image banner 1
        JSONObject banner1 = root.getJSONObject("imageBanner1");
        Map<String, Object> banner1Map = banner1.toMap();
        if(banner1Map.get("imageParameter") != null) {
            try {
                fileUploadProvider.deleteFile(banner1Map.get("imageUrl").toString());
                Part filePart = request.getPart(banner1Map.get("imageParameter").toString());
                imgUploadFutures.add(fileUploadProvider.asyncUpload1File("banner1/", filePart).thenAccept(url -> banner1Map.put("imageUrl", url)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ServletException e) {
                throw new RuntimeException(e);
            }
        }
        banner1Map.remove("imageParameter");

        // Upload image banner 2
        JSONObject banner2 = root.getJSONObject("imageBanner2");
        Map<String, Object> banner2Map = banner2.toMap();
        if(banner2Map.get("imageParameter") != null) {
            try {
                fileUploadProvider.deleteFile(banner2Map.get("imageUrl").toString());
                Part filePart = request.getPart(banner2Map.get("imageParameter").toString());
                imgUploadFutures.add(fileUploadProvider.asyncUpload1File("banner2/", filePart).thenAccept(url -> banner2Map.put("imageUrl", url)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ServletException e) {
                throw new RuntimeException(e);
            }
        }
        banner2Map.remove("imageParameter");

        //check id category into database
        List<Long> listCatId = categories.stream().map(id -> Long.parseLong(id.toString())).collect(java.util.stream.Collectors.toList());
        List<CategoryEntity> entityList = this.categoryRepository.findAllByIdIn(listCatId);
        if(entityList.size() != listCatId.size()) {
            throw new RuntimeException("Exist Category not found");
        }

        if (!imgUploadFutures.isEmpty()) {
            try {
                CompletableFuture.allOf(imgUploadFutures.toArray(CompletableFuture[]::new)).get();
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

        // check option key is exist, if exist, update, else add new
        if(optionsCheck == null) {
            return this.optionsRepository.save(optionsEntity);
        } else {
            optionsCheck.setOptionValue(new JSONObject(finalRs).toString());
            return this.optionsRepository.save(optionsCheck);
        }
    }

    @Override
    public OptionsEntity getOptionByKey(String key) {
        return this.optionsRepository.findByOptionKey(key).orElseThrow(() -> new RuntimeException("Options not found"));
    }
    @Override
    public List<OptionsEntity> getOptionsByKeys(List<String> keys) {
        return keys.stream().map(key -> this.getOptionByKey(key)).collect(Collectors.toList());
    }
}
