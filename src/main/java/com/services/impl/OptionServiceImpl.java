package com.services.impl;

import com.entities.OptionsEntity;
import com.models.OptionsModel;
import com.services.IOptionsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class OptionServiceImpl implements IOptionsService {

    @Override
    public List<OptionsEntity> findAll() {
        return null;
    }

    @Override
    public Page<OptionsEntity> findAll(Pageable page) {
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
}
