package com.services.impl;

import com.entities.*;
import com.models.CartModel;
import com.repositories.ICartDetailRepository;
import com.repositories.ICartRepository;
import com.repositories.IProductRepository;
import com.repositories.IProductSkuRepository;
import com.services.ICartService;
import com.utils.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements ICartService {
    final private ICartRepository cartRepository;
    final private IProductRepository productRepository;
    final private ICartDetailRepository cartDetailRepository;
    final private IProductSkuRepository productSkuRepository;

    public CartServiceImpl(ICartRepository cartRepository, IProductRepository productRepository, ICartDetailRepository cartDetailRepository, IProductSkuRepository productSkuRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.cartDetailRepository = cartDetailRepository;
        this.productSkuRepository = productSkuRepository;
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
        ProductEntity productEntity = this.productRepository.findById(model.getProductId()).orElseThrow(() -> new RuntimeException("Product not found"));
        UserEntity userEntity = SecurityUtils.getCurrentUser().getUser();
        ProductSkuEntity productSkuEntity = this.productSkuRepository.findById(model.getSkuId()).orElseThrow(() -> new RuntimeException("Product sku not found"));

        CartEntity cart = this.cartRepository.findByProduct_IdAndUser_Id(model.getProductId(), userEntity.getId())
                .orElse(CartEntity.builder().product(productEntity).user(userEntity).cartDetails(new ArrayList<>()).build());

        CartDetailEntity cartDetailEntity = cart.getCartDetails().stream().filter(cd -> cd.getSku().getId().equals(model.getSkuId())).findFirst().orElse(null);

        if (cartDetailEntity == null) {
            cartDetailEntity = CartDetailEntity.builder()
                    .sku(productSkuEntity)
                    .quantity(model.getQuantity())
                    .cart(cart)
                    .build();
            cart.getCartDetails().add(cartDetailEntity);
        } else {
            cartDetailEntity.setQuantity(cartDetailEntity.getQuantity() + model.getQuantity());
        }

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
    public boolean deleteById(Long id) {
        CartEntity originCart = this.findById(id);
        originCart.getCartDetails().forEach(cd -> this.cartDetailRepository.delete(cd));
        this.cartRepository.deleteById(id);
        return true;
    }

    @Override
    public boolean deleteByIds(List<Long> ids) {
        return false;
    }

    @Override
    public CartEntity editSku(CartModel model) {
        CartEntity originCart = this.findById(model.getId());
        originCart.getCartDetails().stream().filter(cd -> cd.getSku().getId().equals(model.getSkuId())).findFirst().ifPresent(cd -> {
            cd.setSku(this.productSkuRepository.findById(model.getSkuId()).orElseThrow(() -> new RuntimeException("Product sku not found")));
        });
        return this.cartRepository.save(originCart);
    }

    @Override
    public CartEntity editQuantity(CartModel model) {
        CartEntity originCart = this.findById(model.getId());
        originCart.getCartDetails().stream().filter(cd -> cd.getSku().getId().equals(model.getSkuId())).findFirst().ifPresent(cd -> {
            cd.setQuantity(model.getQuantity());
        });
        return this.cartRepository.save(originCart);
    }

    public static void main(String[] args) {
        CartModel model = new CartModel();
        model.setId(1L);
        model.setSkuId(1L);
        model.setQuantity(1);
        model.setProductId(1L);
        System.out.println(model.toString() + "o o o ");
    }
}
