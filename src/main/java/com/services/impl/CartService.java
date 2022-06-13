package com.services.impl;

import com.entities.CartEntity;
import com.entities.ProductEntity;
import com.entities.UserEntity;
import com.models.CartModel;
import com.repositories.ICartRepository;
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
public class CartService implements ICartService {
    private final ICartRepository cartRepository;
    private final IProductService productService;
    private final IUserService userService;

    public CartService(ICartRepository cartRepository, IProductService productService, IUserService userService) {
        this.cartRepository = cartRepository;
        this.productService = productService;
        this.userService = userService;
    }

    @Override
    public List<CartEntity> findAll() {
        return null;
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
        return this.cartRepository.findById(id).orElseThrow(() -> new RuntimeException("Cart not found"));
    }

    @Override
    public CartEntity add(CartModel model) {
        CartEntity cart = CartModel.toEntity(model);

        ProductEntity productEntity = this.productService.findById(model.getProductId());
        productEntity.getOptions().stream().forEach(option -> {
            if (option.getId() == (model.getOptionId())) {
                cart.setOptionId(model.getOptionId());
            }
        });
        if(cart.getOptionId() == null) {
            throw new RuntimeException("Option not found");
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
        CartEntity cartOrigin = this.findById(model.getId());
        cartOrigin.setQuantity(model.getQuantity());
        return cartOrigin;
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
