package com.services.impl;

import com.entities.ProductMetaEntity;
import com.models.ProductMetaModel;
import com.services.IProductMetaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductMetaServiceImpl implements IProductMetaService {

    @Override
    public List<ProductMetaEntity> findAll() {
        return null;
    }

    @Override
    public Page<ProductMetaEntity> findAll(Pageable page) {
        return null;
    }

    @Override
    public Page<ProductMetaEntity> filter(Pageable page, Specification<ProductMetaEntity> specs) {
        return null;
    }

    @Override
    public ProductMetaEntity findById(Long id) {
        return null;
    }

    @Override
    public ProductMetaEntity add(ProductMetaModel model) {
        return null;
    }

    @Override
    public List<ProductMetaEntity> add(List<ProductMetaModel> model) {
        return null;
    }

    @Override
    public ProductMetaEntity update(ProductMetaModel model) {
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
