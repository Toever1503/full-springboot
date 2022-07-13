package com.services;

import com.entities.OptionsEntity;
import com.models.OptionsModel;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface IOptionsService extends IBaseService<OptionsEntity, OptionsModel, Long> {
    OptionsEntity settingUpdateHomePage(OptionsModel model, HttpServletRequest request);
    OptionsEntity getOptionByKey(String key);
    List<OptionsEntity> getOptionsByKeys(List<String> keys);
}
