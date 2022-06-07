package com.services.impl;

import com.entities.ProductEntity;
import com.entities.UserLikeProductEntity;
import com.models.ProductModel;
import com.repositories.IProductRepository;
import com.repositories.IUserLikeProductRepository;
import com.services.IProductService;
import com.utils.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class ProductServiceImpl implements IProductService {

    final IProductRepository productRepository;

    final IUserLikeProductRepository userLikeProductRepository;

    public ProductServiceImpl(IProductRepository productRepository, IUserLikeProductRepository userLikeProductRepository) {
        this.productRepository = productRepository;
        this.userLikeProductRepository = userLikeProductRepository;
    }

    @Override
    public List<ProductEntity> findAll() {
        return null;
    }

    @Override
    public Page<ProductEntity> findAll(Pageable page) {
        return productRepository.findAll(page);
    }

    @Override
    public Page<ProductEntity> filter(Pageable page, Specification<ProductEntity> specs) {
        return null;
    }

    @Override
    public ProductEntity findById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
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

    @Override
    public ProductEntity findProductBySlug(String slug) {
        return productRepository.findBySlug(slug).orElseThrow(() -> new RuntimeException("Product not found"));
    }

    @Override
    public int likeProduct(Long id) {
        //If product is present
        if(userLikeProductRepository.findFirstByProductIdAndUserId(id, SecurityUtils.getCurrentUserId())!=null){
            UserLikeProductEntity userLikeProductEntity = userLikeProductRepository.findFirstByProductIdAndUserId(id, SecurityUtils.getCurrentUserId());
            userLikeProductEntity.setIsLike(!userLikeProductEntity.getIsLike());
            return 0;
        }else {
            //if product not present
            UserLikeProductEntity entity = new UserLikeProductEntity();
            entity.setProductId(id);
            entity.setIsLike(true);
            entity.setUserId(SecurityUtils.getCurrentUserId());
            userLikeProductRepository.save(entity);
            return 1;
        }
    }
}
