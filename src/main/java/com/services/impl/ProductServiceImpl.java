package com.services.impl;

import com.entities.ProductEntity;
import com.models.ProductModel;
import com.services.IProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements IProductService {

    @Override
    public List<ProductEntity> findAll() {
        return null;
    }

    @Override
    public Page<ProductEntity> findAll(Pageable page) {
        return null;
    }

    @Override
    public Page<ProductEntity> filter(Pageable page, Specification<ProductEntity> specs) {
        return null;
    }

    @Override
    public ProductEntity findById(Long id) {
        return null;
    }

    @Override
    public ProductEntity add(ProductModel model) {
        return null;
    }

    @Override
    public List<ProductEntity> add(List<ProductModel> model) {
        return null;
    }

    @Override
    public ProductEntity update(ProductModel model) {
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
