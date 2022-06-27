package com.webs;

import com.dtos.CartDto;
import com.dtos.ResponseDto;
import com.models.CartModel;
import com.services.ICartService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
public class CartResources {
    final private ICartService cartService;

    public CartResources(ICartService cartService) {
        this.cartService = cartService;
    }

    @Transactional
    @GetMapping
    public ResponseDto getCart(Pageable pageable) {
        return ResponseDto.of(cartService.findAll(pageable).map(CartDto::toDto), "Get Cart");
    }

    @Transactional
    @GetMapping("{id}")
    public ResponseDto getCartById(@PathVariable Long id) {
        return ResponseDto.of(CartDto.toDto(cartService.findById(id)), "Get Cart by id: " + id);
    }

    @Transactional
    @PostMapping
    public ResponseDto addCart(@RequestBody CartModel cartModel) {
        cartModel.setId(null);
        return ResponseDto.of(CartDto.toDto(cartService.add(cartModel)), "Add Cart");
    }

    @Transactional
    @PutMapping("{id}")
    public ResponseDto editSku(@PathVariable Long id, @RequestBody CartModel cartModel) {
        cartModel.setId(id);
        return ResponseDto.of(CartDto.toDto(cartService.editSku(cartModel)), "Edit Sku in Cart");
    }

//    @Transactional
//    @PutMapping("{id}")
//    public ResponseDto editQuantity(@PathVariable Long id, @RequestBody CartModel cartModel) {
//        cartModel.setId(id);
//        return ResponseDto.of(CartDto.toDto(cartService.editQuantity(cartModel)), "Edit quantity in Cart");
//    }

    @Transactional
    @DeleteMapping("{id}")
    public ResponseDto deleteCart(@PathVariable Long id) {
        return ResponseDto.of(cartService.deleteById(id), "Delete Cart");
    }
}
