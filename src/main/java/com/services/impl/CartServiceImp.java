package com.services.impl;

import com.entities.CartEntity;
import com.entities.OptionEntity;
import com.entities.ProductEntity;
import com.entities.UserEntity;
import com.models.CartModel;
import com.models.ChangeOptionProductModel;
import com.repositories.ICartRepository;
import com.repositories.IOptionsRepository;
import com.services.ICartService;
import com.services.IProductService;
import com.services.IUserService;
import com.utils.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartServiceImp implements ICartService {
    private final ICartRepository cartRepository;
    private final IProductService productService;
    private final IUserService userService;
    private final IOptionsRepository optionsRepository;

    public CartServiceImp(ICartRepository cartRepository, IProductService productService, IUserService userService, IOptionsRepository optionsRepository) {
        this.cartRepository = cartRepository;
        this.productService = productService;
        this.userService = userService;
        this.optionsRepository = optionsRepository;
    }

    @Override
    public List<CartEntity> findAll() {
        return this.cartRepository.findAll();
    }

    @Override
    public Page<CartEntity> findAll(Pageable page) {
        return this.cartRepository.findAll(page);
    }

    @Override
    public Page<CartEntity> filter(Pageable page, Specification<CartEntity> specs) {
        return null;
    }

    @Override
    public CartEntity findById(Long id) {
        return this.cartRepository.findById(id).orElseThrow(() -> new RuntimeException("Cart not found, id: " + id));
    }

    @Override
    public CartEntity add(CartModel model) {
        // check product exist
        ProductEntity productEntity = this.productService.findById(model.getProductId());
        // check product exist into cart by product id and option id
        CartEntity cartOrigin = this.cartRepository.findAllByProductIdAndOptionId(model.getProductId(), model.getOptionId());
        if(cartOrigin != null){
            if(cartOrigin.getUser().getId().equals(SecurityUtils.getCurrentUserId())) {
                cartOrigin.setQuantity(cartOrigin.getQuantity() + model.getQuantity());
                return this.cartRepository.save(cartOrigin);
            }
        }

        CartEntity cart = CartModel.toEntity(model);
        // check option in product or not ?
        if(productEntity.getOptions().stream().anyMatch(option -> option.getId().equals(model.getOptionId()))) {
            cart.setOptionId(model.getOptionId());
        }else{
            throw new RuntimeException("Option not found, id: " + model.getOptionId());
        }

        cart.setProduct(productEntity);

        UserEntity userEntity = SecurityUtils.getCurrentUser().getUser();
        cart.setUser(userEntity);

        return this.cartRepository.save(cart);
    }

    @Override
    public List<CartEntity> add(List<CartModel> model) {
        return null;
    }

    @Override
    public CartEntity update(CartModel model) {
        return null;
    }

    @Override
    public CartEntity updateQuantityProduct(Long id, Integer quantity) {
        CartEntity cartEntity = this.findById(id);
        cartEntity.setQuantity(quantity);
        return this.cartRepository.save(cartEntity);
    }

    @Override
    public Page<CartEntity> findAllByUserId(Pageable pageable) {
        return this.cartRepository.findAllByUserId(SecurityUtils.getCurrentUserId(), pageable);
    }

    @Override
    public CartEntity updateOptionProduct(Long id, ChangeOptionProductModel model) {
        CartEntity cartEntity = this.findById(id);
        if(SecurityUtils.getCurrentUserId().equals(cartEntity.getUser().getId())) {
            ProductEntity product = this.productService.findById(model.getProductId());
            if(product.getOptions().stream().anyMatch(option1 -> option1.getId().equals(model.getOptionId()))) {
                cartEntity.setOptionId(model.getOptionId());
                return this.cartRepository.save(cartEntity);
            }else {
                throw new RuntimeException("Option not found, id: " + model.getOptionId());
            }
        }
       throw new RuntimeException("You are not owner of this cart");
    }

    @Override
    public boolean deleteById(Long id) {
        this.cartRepository.deleteById(id);
        return true;
    }

    @Override
    public boolean deleteByIds(List<Long> ids) {
        ids.stream().forEach(id -> this.deleteById(id));
        return true;
    }


}
