package com.webs;

import com.dtos.CartDto;
import com.dtos.ResponseDto;
import com.models.CartModel;
import com.models.ChangeOptionModel;
import com.services.ICartService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;

@RestController
@RequestMapping("/carts")
public class CartResources {
    final private ICartService cartService;

    public CartResources(ICartService cartService) {
        this.cartService = cartService;
    }

    @Transactional
    @GetMapping
    public ResponseDto getCart(Pageable pageable) {
        return ResponseDto.of(cartService.findAll(pageable).map(CartDto::toDto), "Lấy tất cả các giỏ hàng");
    }

    @Transactional
    @GetMapping("{id}")
    public ResponseDto getCartById(@PathVariable Long id) {
        return ResponseDto.of(CartDto.toDto(cartService.findById(id)), "Lấy giỏ hàng theo id: " + id);
    }

    @Transactional
    @PostMapping
    public ResponseDto addCart(@RequestBody CartModel cartModel) {
        cartModel.setId(null);
        return ResponseDto.of(CartDto.toDto2(cartService.add(cartModel), cartModel.getSkuId()), "Tạo giỏ hàng");
    }

    @Transactional
    @PutMapping("change-option/{id}")
    public ResponseDto editSku(@PathVariable Long id, @RequestBody ChangeOptionModel changeOptionModel) {
        changeOptionModel.setId(id);
        return ResponseDto.of(CartDto.toDto(cartService.changeOption(changeOptionModel)), "Sửa sku trong giỏ hàng, id: " + id);
    }

    @Transactional
    @PutMapping("change-quantity/{id}")
    public ResponseDto editQuantity(@PathVariable Long id, @RequestBody CartModel cartModel) {
        cartModel.setId(id);
        return ResponseDto.of(CartDto.toDto(cartService.editQuantity(cartModel)), "Sửa số lượng trong giỏ hàng, id: " + id);
    }

    @Transactional
    @DeleteMapping("{id}")
    public ResponseDto deleteCart(@PathVariable Long id) {
        return ResponseDto.of(cartService.deleteById(id), "Xoá giỏ hàng id: " + id);
    }

    @Transactional
    @DeleteMapping("sku/{idCart}/{idSku}")
    public ResponseDto deleteCartByIdProductAndIdSku(@PathVariable("idCart") Long idCart, @PathVariable("idSku") Long idSku) {
        return ResponseDto.of(cartService.removeCartByIdCartAndIdSku(idCart, idSku), "Xoá giỏ hàng qua id giỏ hàng: " + idCart + " và idSku: " + idSku);
    }
}
