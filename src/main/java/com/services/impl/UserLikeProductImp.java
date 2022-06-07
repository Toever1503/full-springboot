package com.services.impl;

import com.entities.UserLikeProductEntity;
import com.models.UserLikeProductModel;
import com.repositories.IUserLikeProductRepository;
import com.services.IUserLikeProductService;
import com.utils.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserLikeProductImp implements IUserLikeProductService {

    final IUserLikeProductRepository userLikeProductRepository;

    public UserLikeProductImp(IUserLikeProductRepository userLikeProductRepository) {
        this.userLikeProductRepository = userLikeProductRepository;
    }

    @Override
    public List<UserLikeProductEntity> findAll() {
        return null;
    }

    @Override
    public Page<UserLikeProductEntity> findAll(Pageable page) {
        return null;
    }

    @Override
    public Page<UserLikeProductEntity> filter(Pageable page, Specification<UserLikeProductEntity> specs) {
        return null;
    }

    @Override
    public UserLikeProductEntity findById(Long id) {
        return null;
    }

    @Override
    public UserLikeProductEntity add(UserLikeProductModel model) {
        UserLikeProductEntity entity = new UserLikeProductEntity();
        entity.setProductId(model.getProductId());
        entity.setUserId(SecurityUtils.getCurrentUserId());
        entity.setIsLike(true);
        return userLikeProductRepository.save(entity);
    }

    @Override
    public List<UserLikeProductEntity> add(List<UserLikeProductModel> model) {
        return null;
    }

    @Override
    public UserLikeProductEntity update(UserLikeProductModel model) {
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
    public int likeProduct(Long id) {
        if(userLikeProductRepository.findFirstByProductIdAndUserId(id, SecurityUtils.getCurrentUserId())!=null){
            UserLikeProductEntity userLikeProductEntity = userLikeProductRepository.findFirstByProductIdAndUserId(id, SecurityUtils.getCurrentUserId());
            userLikeProductEntity.setIsLike(!userLikeProductEntity.getIsLike());
            return 0;
        }else {
            UserLikeProductEntity entity = new UserLikeProductEntity();
            entity.setProductId(id);
            entity.setIsLike(true);
            entity.setUserId(SecurityUtils.getCurrentUserId());
            userLikeProductRepository.save(entity);
            return 1;
        }
    }
}
