package com.webs;

import com.dtos.OrderDto;
import com.dtos.ResponseDto;
import com.entities.OrderEntity;
import com.models.OrderByStatusAndTimeModel;
import com.models.OrderModel;
import com.models.TotalOrderModel;
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
        OrderEntity order =  orderService.add(orderModel);
        return ResponseDto.of(OrderDto.toDto(order), "Create order");
    }

    @Transactional
    @PatchMapping("update-delivery-code/{id}")
    public ResponseDto updateDeliveryCode(@PathVariable("id") Long id, @RequestParam(name = "code") @Valid @NotBlank String deliveryCode) {
        OrderEntity order = orderService.updateDeliveryCode(id, deliveryCode);
        return ResponseDto.of(OrderDto.toDto(order), "Update delivery code");
    }
    @Transactional
    @PatchMapping("update-status/{id}")
    public ResponseDto updateStatusOrder(@PathVariable("id") Long id, @RequestParam("status") String status) {
        OrderEntity order = this.orderService.updateStatusOrder(id, status);
        return ResponseDto.of(OrderDto.toDto(order), "Update status order, id: " + id);
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseDto deleteOrder(@PathVariable("id") Long id) {
        OrderEntity order = this.orderService.cancelOrder(id);
        return ResponseDto.of(OrderDto.toDto(order), "Delete order id: " + id);
    }

    @Transactional
    @PostMapping("filter")
    public ResponseDto filter(@RequestBody OrderFilterModel orderFilterModel, Pageable page) {
        return ResponseDto.of(this.orderService.filter(page, Specification.where(OrderSpecification.filter(orderFilterModel))).map(OrderDto::toDto), "Filter success");
    }

    @Transactional
    @GetMapping("/check/{id}")
    public ResponseDto getPayStatus(@PathVariable("id") Long id){
        return ResponseDto.of(orderService.getStatusByID(id),"Get status");
    }

    @Transactional
    @GetMapping("/redirect/{id}")
    public ResponseDto getPayUrl(@PathVariable("id") Long id){
        return ResponseDto.of(orderService.getUrlByID(id),"Get url");
    }

    // chua xu ly duoc du lieu dau ra
    @RolesAllowed("ADMINISTRATOR")
    @Transactional
    @PostMapping("/report/order-by-time-and-status")
    public ResponseDto getTotalOrder(@RequestBody OrderByStatusAndTimeModel model){
        return ResponseDto.of(this.orderService.getTotalOrderByStatusAndTime(model.getStatus_order(), model.getTime_from(), model.getTime_to()),"Get total order by status and time");
    }

    @RolesAllowed("ADMINISTRATOR")
    @Transactional
    @PostMapping("/report/total-order-by-time-and-status")
    public ResponseDto getTotalOrderByStatus(@RequestBody TotalOrderModel model){
        return ResponseDto.of(this.orderService.getTotalOrderByStatus(model.getStatus(), model.getTime_from(), model.getTime_to()),"Get total order by status");
    }
}
