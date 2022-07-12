package com.services;

import com.entities.OptionsEntity;
import com.models.OptionsModel;

import javax.servlet.http.HttpServletRequest;

public interface IOptionsService extends IBaseService<OptionsEntity, OptionsModel, Long> {
    OptionsEntity settingUpdateHomePage(OptionsModel model, HttpServletRequest request);

}
