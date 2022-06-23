package com.webs;

import com.dtos.CartDto;
import com.dtos.ResponseDto;
import com.models.CartModel;
import com.models.ChangeOptionProductModel;
import com.repositories.IOptionsRepository;
import com.services.ICartService;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/carts")
public class CartResources {
    private final ICartService cartService;
    private final IOptionsRepository optionsRepository;

    public CartResources(ICartService cartService, IOptionsRepository optionsRepository) {
        this.cartService = cartService;
        this.optionsRepository = optionsRepository;
    }

    @Transactional
    @GetMapping
    public ResponseDto getAllCarts(Pageable pageable) {
        return ResponseDto.of(this.cartService.findAllByUserId(pageable).map(CartDto::toDto), "Get all carts successfully");
    }

    @Transactional
    @GetMapping("/{id}")
    public ResponseDto getCartById(@PathVariable("id") Long id) {
        return ResponseDto.of(CartDto.toDto(this.cartService.findById(id)), "Get cart by id successfully");
    }

    @Transactional
    @PostMapping
    public ResponseDto addCart(@Valid @RequestBody CartModel model) {
        return ResponseDto.of(CartDto.toDto(this.cartService.add(model)), "added cart successfully");
    }

    @Transactional
    @PatchMapping("{id}")
    public ResponseDto updateQuantityProduct(@RequestParam("quantity") @Valid @Min(1) Integer quantity, @PathVariable("id") Long id) {
        return ResponseDto.of(CartDto.toDto(this.cartService.updateQuantityProduct(id, quantity)), "updated quantity product in cart successfully");
    }

    @Transactional
    @PatchMapping("/change-option/{id}")
    public ResponseDto updateOptionProduct(@PathVariable("id") Long id, @RequestBody ChangeOptionProductModel model) {
        return ResponseDto.of(CartDto.toDto(this.cartService.updateOptionProduct(id, model)), "updated option product in cart successfully");
    }

    @Transactional
    @DeleteMapping("{id}")
    public ResponseDto deleteCart(@PathVariable("id") Long id) {
        return ResponseDto.of(this.cartService.deleteById(id), "Cart deleted successfully");
    }

    @Transactional
    @DeleteMapping("/delete-list")
    public ResponseDto deleteList( @RequestBody List<Long> ids) {
        return ResponseDto.of(this.cartService.deleteByIds(ids), "Carts deleted successfully");
    }
}
