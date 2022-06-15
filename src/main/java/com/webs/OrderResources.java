package com.webs;

import com.dtos.OrderDto;
import com.dtos.ResponseDto;
import com.models.OrderModel;
import com.models.filters.OrderFilterModel;
import com.models.specifications.OrderSpecification;
import com.services.IOrderService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/order")
public class OrderResources {
    private final IOrderService orderService;

    public OrderResources(IOrderService orderService) {
        this.orderService = orderService;
    }

    @Transactional
    @GetMapping("/{id}")
    public ResponseDto getOrderById(@PathVariable("id") Long id) {
        return ResponseDto.of(orderService.findById(id), "Get order by id success, id: " + id);
    }

    @Transactional
    @PostMapping
    public ResponseDto createOrder(@RequestBody OrderModel orderModel) {
        return ResponseDto.of(OrderDto.toDto(orderService.add(orderModel)), "Create order success");
    }

    @Transactional
    @PostMapping("update-status/{id}")
    public ResponseDto updateStatusOrder(@PathVariable("id") Long id, @RequestParam("status") String status) {
        return ResponseDto.of(OrderDto.toDto(this.orderService.updateStatusOrder(id, status)), "Update status order, id: " + id);
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseDto deleteOrder(@PathVariable("id") Long id) {
        return ResponseDto.of(OrderDto.toDto(orderService.cancelOrder(id)), "Delete order id: " + id);
    }

    @Transactional
    @PostMapping("filter")
    public ResponseDto filter(@RequestBody OrderFilterModel orderFilterModel, Pageable page) {
        return ResponseDto.of(this.orderService.filter(page, Specification.where(OrderSpecification.filter(orderFilterModel))).map(OrderDto::toDto), "Filter success");
    }
}
