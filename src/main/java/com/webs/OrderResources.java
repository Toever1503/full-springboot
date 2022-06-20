package com.webs;

import com.dtos.OrderDto;
import com.dtos.ResponseDto;
import com.models.OrderModel;
import com.models.filters.OrderFilterModel;
import com.models.specifications.OrderSpecification;
import com.services.IOrderService;
import com.utils.SecurityUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@RestController
@RequestMapping("/order")
public class OrderResources {
    private final IOrderService orderService;

    public OrderResources(IOrderService orderService) {
        this.orderService = orderService;
    }

    @RolesAllowed("ADMINISTRATOR")
    @Transactional
    @GetMapping("/{id}")
    public ResponseDto getOrderById(@PathVariable("id") Long id) {
        return ResponseDto.of(OrderDto.toDto(orderService.findById(id)), "Get order by id success, id: " + id);
    }

    @Transactional
    @GetMapping("/user/{id}")
    public ResponseDto getOrderUserById(@PathVariable("id") Long id) {
        return ResponseDto.of(OrderDto.toDto(orderService.onlyUserFindById(id, SecurityUtils.getCurrentUserId())), "User id: " + SecurityUtils.getCurrentUserId() + ", Get order by id success, id: " + id);
    }

    @RolesAllowed("ADMINISTRATOR")
    @Transactional
    @GetMapping
    public ResponseDto getAllOrders(Pageable pageable) {
        return ResponseDto.of(orderService.findAll(pageable).map(OrderDto::toDto), "User id: " + SecurityUtils.getCurrentUserId() + ", Get all orders successfully");
    }

    @Transactional
    @GetMapping("user")
    public ResponseDto getAllOrdersUser(Pageable pageable) {
        return ResponseDto.of(orderService.onlyUserFindAll(pageable, SecurityUtils.getCurrentUserId()).map(OrderDto::toDto), "User id: " + SecurityUtils.getCurrentUserId() + ", Get all orders user");
    }

    @Transactional
    @PostMapping
    public ResponseDto createOrder(@RequestBody OrderModel orderModel) {
        return ResponseDto.of(OrderDto.toDto(orderService.add(orderModel)), "Create order success");
    }

    @Transactional
    @PatchMapping("update-delivery-code/{id}")
    public ResponseDto updateDeliveryCode(@PathVariable("id") Long id, @RequestParam(name = "code") @Valid @NotBlank String deliveryCode) {
        return ResponseDto.of(orderService.updateDeliveryCode(id, deliveryCode), "Update delivery code");
    }
    @Transactional
    @PatchMapping("update-status/{id}")
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

    @GetMapping("/check/{id}")
    public ResponseDto getPayStatus(@PathVariable("id") Long id){
        return ResponseDto.of(orderService.getStatusByID(id),"Get status");
    }

    @GetMapping("/redirect/{id}")
    public ResponseDto getPayUrl(@PathVariable("id") Long id){
        return ResponseDto.of(orderService.getUrlByID(id),"Get url");
    }
}
