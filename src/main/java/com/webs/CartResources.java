package com.webs;

import com.dtos.CartDto;
import com.dtos.OptionDto;
import com.dtos.ResponseDto;
import com.entities.CartEntity;
import com.models.CartModel;
import com.repositories.IOptionsRepository;
import com.services.ICartService;
import com.services.IOptionsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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
        Page<CartEntity> carts = cartService.findAll(pageable);
        Page<CartDto> cartDtos = carts.map(CartDto::toDto);
        carts.getContent().stream().forEach(cart -> {
             OptionDto optionDto = OptionDto.toDto(this.optionsRepository.findById(cart.getOptionId()).orElse(null));
             cartDtos.getContent().stream().forEach(cartDto -> {
                 if (cartDto.getId() == cart.getId()) {
                     cartDto.setOption(optionDto);
                 }
             });
        });
        return ResponseDto.of(cartDtos, "Get all carts successfully");
    }

    @Transactional
    @GetMapping("/{id}")
    public ResponseDto getCartById(@PathVariable("id") Long id) {
        CartEntity cartEntity = this.cartService.findById(id);
        OptionDto optionDto = OptionDto.toDto(this.optionsRepository.findById(cartEntity.getOptionId()).get());
        CartDto cartDto = CartDto.toDto(cartEntity);
        cartDto.setOption(optionDto);

        return ResponseDto.of(cartDto, "Get cart by id successfully");
    }

    @Transactional
    @PostMapping
    public ResponseDto addCart(@Valid @RequestBody CartModel model) {
        model.setId(null);
        CartEntity cartEntity = this.cartService.add(model);
        OptionDto optionDto = OptionDto.toDto(this.optionsRepository.findById(cartEntity.getOptionId()).get());
        CartDto cartDto = CartDto.toDto(cartEntity);
        cartDto.setOption(optionDto);
        return ResponseDto.of(cartDto, "Cart added successfully");
    }

    @Transactional
    @PatchMapping("{id}")
    public ResponseDto updateCart(@Valid @RequestBody CartModel model, @PathVariable("id") Long id) {
        model.setId(id);
        CartEntity cartEntity = this.cartService.update(model);
        OptionDto optionDto = OptionDto.toDto(this.optionsRepository.findById(cartEntity.getOptionId()).get());
        CartDto cartDto = CartDto.toDto(cartEntity);
        cartDto.setOption(optionDto);
        return ResponseDto.of(cartDto, "Cart updated successfully");
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
