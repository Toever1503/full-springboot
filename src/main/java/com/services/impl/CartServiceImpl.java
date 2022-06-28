package com.services.impl;

import com.entities.*;
import com.models.CartModel;
import com.models.ChangeOptionModel;
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
        return this.cartRepository.findAllByUser_Id(SecurityUtils.getCurrentUserId(), page);
    }

    @Override
    public Page<CartEntity> filter(Pageable page, Specification<CartEntity> specs) {
        return null;
    }

    @Override
    public CartEntity findById(Long id) {
        return this.cartRepository.findByUser_IdAndId(SecurityUtils.getCurrentUserId(), id).orElseThrow(() -> new RuntimeException("Cart not found"));
    }

    @Override
    public CartEntity add(CartModel model) {
        ProductEntity productEntity = this.productRepository.findById(model.getProductId()).orElseThrow(() -> new RuntimeException("Product not found"));
        UserEntity userEntity = SecurityUtils.getCurrentUser().getUser();
        ProductSkuEntity productSkuEntity = this.productSkuRepository.findById(model.getSkuId()).orElseThrow(() -> new RuntimeException("Product sku not found"));

        CartEntity cart = this.cartRepository.findByProduct_IdAndUser_Id(model.getProductId(), userEntity.getId())
                .orElse(CartEntity.builder().product(productEntity).user(userEntity).cartDetails(new ArrayList<>()).build());

        CartDetailEntity cartDetailEntity = cart.getCartDetails().stream().filter(cd -> cd.getSku().getId().equals(model.getSkuId())).findFirst().orElse(null);

        if (productSkuEntity.getProduct().getId().equals(model.getProductId())) {
            if (model.getQuantity() > productSkuEntity.getInventoryQuantity()) {
                throw new RuntimeException("Product quantity is not enough");
            }
            if (cartDetailEntity == null) {
                cartDetailEntity = CartDetailEntity.builder()
                        .sku(productSkuEntity)
                        .quantity(model.getQuantity())
                        .cart(cart)
                        .build();
                cart.getCartDetails().add(cartDetailEntity);
            } else {
                cartDetailEntity.setQuantity(cartDetailEntity.getQuantity() + model.getQuantity());
                if (cartDetailEntity.getQuantity() > productSkuEntity.getInventoryQuantity()) {
                    throw new RuntimeException("Product quantity is not enough");
                }
            }
        } else {
            throw new RuntimeException("Product sku not found");
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
        if (originCart.getUser().getId().equals(SecurityUtils.getCurrentUser().getUser().getId())) {
            originCart.getCartDetails().forEach(cd -> this.cartDetailRepository.delete(cd));
            this.cartRepository.deleteById(id);
            return true;
        } else {
            throw new RuntimeException("You are not allowed to delete this cart");
        }
    }

    @Override
    public boolean deleteByIds(List<Long> ids) {
        return false;
    }

    @Override
    public CartEntity changeOption(ChangeOptionModel model) {
        CartEntity originCart = this.findById(model.getId());

        if (originCart.getUser().getId().equals(SecurityUtils.getCurrentUser().getUser().getId())) {
            // check cart co product hay khong ?
            if (originCart.getProduct().getId().equals(model.getProductId())) {
                // check xem trong cart co option nay hay khong ? neu co thi se xoa sku cu va set quantity option cu co trong cart = cu + quantity option moi
                CartDetailEntity checkedCart = originCart.getCartDetails().stream().filter(cd -> cd.getSku().getId().equals(model.getSkuIdNew())).findFirst().orElse(null);
                if (checkedCart != null) {
                    checkedCart.setQuantity(model.getQuantity() + checkedCart.getQuantity());
                    if (checkedCart.getQuantity() > checkedCart.getSku().getInventoryQuantity()) {
                        throw new RuntimeException("Product quantity is not enough");
                    }
                    originCart.getCartDetails().removeIf(cd -> cd.getSku().getId().equals(model.getSkuIdOld()));
                } else {
                    // neu khong co thi se them option moi vao cart neu nhu option do thuoc product do va xoa option cu
                    this.productSkuRepository.findByProduct_Id(model.getProductId()).forEach(sku -> {
                        if (sku.getId().equals(model.getSkuIdNew())) {
                            originCart.getCartDetails().add(CartDetailEntity.builder()
                                    .sku(sku)
                                    .quantity(model.getQuantity() > sku.getInventoryQuantity() ? sku.getInventoryQuantity() : model.getQuantity())
                                    .cart(originCart)
                                    .build());
                        }
                    });
                    originCart.getCartDetails().removeIf(cd -> cd.getSku().getId().equals(model.getSkuIdOld()));
                }
            } else {
                throw new RuntimeException("Product not found");
            }
            return this.cartRepository.save(originCart);
        } else {
            throw new RuntimeException("You are not allowed to change this cart");
        }

    }

    @Override
    public CartEntity editQuantity(CartModel model) {
        CartEntity originCart = this.findById(model.getId());

        if (originCart.getUser().getId().equals(SecurityUtils.getCurrentUser().getUser().getId())) {
            if (originCart.getProduct().getId().equals(model.getProductId())) {
                if (originCart.getProduct().getSkus().stream().filter(sku -> sku.getId().equals(model.getSkuId())).findFirst().orElse(null) == null) {
                    throw new RuntimeException("Product sku not found");
                }
                originCart.getCartDetails().stream().filter(cd -> cd.getSku().getId().equals(model.getSkuId())).findFirst().ifPresent(cd -> {
                    if (model.getQuantity() > cd.getSku().getInventoryQuantity()) {
                        throw new RuntimeException("Product quantity is not enough");
                    }
                    cd.setQuantity(model.getQuantity());
                    this.cartDetailRepository.save(cd);
                });
            } else {
                throw new RuntimeException("Product not found");
            }
            return this.cartRepository.save(originCart);
        } else {
            throw new RuntimeException("You are not allowed to change this cart");
        }

    }

    @Override
    public Boolean removeCartByIdCartAndIdSku(Long idCart, Long idSku) {
        CartEntity originCart = this.findById(idCart);
        if (originCart.getUser().getId().equals(SecurityUtils.getCurrentUser().getUser().getId())) {
            CartDetailEntity cartDetailEntity = this.cartDetailRepository.findCartDetailEntityByCart_IdAndSku_Id(idCart, idSku).orElseThrow(() -> new RuntimeException("Cart detail not found"));
            originCart.getCartDetails().remove(cartDetailEntity);
            if(originCart.getCartDetails().size() == 0) {
                this.cartRepository.deleteById(idCart);
            }
            return true;
        } else {
            throw new RuntimeException("You are not allowed to change this cart");
        }
    }
}
