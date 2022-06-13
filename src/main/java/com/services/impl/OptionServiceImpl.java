package com.services.impl;

import com.entities.OptionEntity;
import com.models.OptionModel;
import com.services.IOptionsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class OptionServiceImpl implements IOptionsService {

    @Override
    public List<OptionEntity> findAll() {
        return null;
    }

    @Override
    public Page<OptionEntity> findAll(Pageable page) {
        return null;
    }

    @Override
    public Page<OptionEntity> filter(Pageable page, Specification<OptionEntity> specs) {
        return null;
    }

    @Override
    public OptionEntity findById(Long id) {
        return null;
    }

    @Override
    public OptionEntity add(OptionModel model) {
        return null;
    }

    @Override
    public List<OptionEntity> add(List<OptionModel> model) {
        return null;
    }

    @Override
    public OptionEntity update(OptionModel model) {
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
