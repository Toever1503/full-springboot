package com.webs;

import com.dtos.OrderDto;
import com.dtos.ResponseDto;
import com.entities.OrderEntity;
import com.entities.RoleEntity;
import com.models.OrderModel;
import com.models.TotalOrderModel;
import com.models.TotalOrderSelectStatusModel;
import com.models.TotalUserModel;
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
import java.text.ParseException;

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
        return ResponseDto.of(OrderDto.toDto(orderService.findById(id)), "lấy đơn hàng theo id: " + id);
    }

    @Transactional
    @GetMapping("/user/{id}")
    public ResponseDto getOrderUserById(@PathVariable("id") Long id) {
        return ResponseDto.of(OrderDto.toDto(orderService.onlyUserFindById(id, SecurityUtils.getCurrentUserId())), "User id: " + SecurityUtils.getCurrentUserId() + ", lấy đơn hàng người dùng theo id: " + id);
    }

    @RolesAllowed("ADMINISTRATOR")
    @Transactional
    @GetMapping
    public ResponseDto getAllOrders(Pageable pageable) {
        return ResponseDto.of(orderService.findAll(pageable).map(OrderDto::toDto), "User id: " + SecurityUtils.getCurrentUserId() + ", lấy tất cả đơn hàng");
    }

    @Transactional
    @GetMapping("user")
    public ResponseDto getAllOrdersUser(Pageable pageable) {
        return ResponseDto.of(orderService.onlyUserFindAll(pageable, SecurityUtils.getCurrentUserId()).map(OrderDto::toDto), "User id: " + SecurityUtils.getCurrentUserId() + ", lấy tất cả đơn hàng của người dùng");
    }

    @Transactional
    @PostMapping
    public ResponseDto createOrder(@RequestBody OrderModel orderModel) {
        OrderEntity order = orderService.add(orderModel);
        return ResponseDto.of(OrderDto.toDto(order), "thêm đơn hàng");
    }

    @Transactional
    @PatchMapping("update-delivery-code/{id}")
    public ResponseDto updateDeliveryCode(@PathVariable("id") Long id, @RequestParam(name = "code") @Valid @NotBlank String deliveryCode) {
        OrderEntity order = orderService.updateDeliveryCode(id, deliveryCode);
        return ResponseDto.of(OrderDto.toDto(order), "sửa mã đơn hàng");
    }

    @Transactional
    @PatchMapping("update-status/{id}")
    public ResponseDto updateStatusOrder(@PathVariable("id") Long id, @RequestParam("status") String status) {
        OrderEntity order = this.orderService.updateStatusOrder(id, status);
        return ResponseDto.of(OrderDto.toDto(order), "sửa trạng thái đơn hàng theo id: " + id);
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseDto deleteOrder(@PathVariable("id") Long id) {
        OrderEntity order = this.orderService.cancelOrder(id);
        return ResponseDto.of(OrderDto.toDto(order), "huỷ đơn hàng: " + id);
    }

    @RolesAllowed(RoleEntity.ADMINISTRATOR)
    @Transactional
    @PostMapping("filter")
    public ResponseDto filter(@RequestBody OrderFilterModel orderFilterModel, Pageable page) throws ParseException {
        return ResponseDto.of(this.orderService.filter(page, Specification.where(OrderSpecification.filter(orderFilterModel))).map(OrderDto::toDto), "lọc đơn hàng");
    }

    @Transactional
    @GetMapping("/check/{id}")
    public ResponseDto getPayStatus(@PathVariable("id") Long id) {
        return ResponseDto.of(orderService.getStatusByID(id), "lấy trạng thái");
    }

    @Transactional
    @GetMapping("/redirect/{id}")
    public ResponseDto getPayUrl(@PathVariable("id") Long id) {
        return ResponseDto.of(orderService.getUrlByID(id), "lấy url");
    }

    @RolesAllowed("ADMINISTRATOR")
    @Transactional
    @PostMapping("/report/all-by-time-and-status")
    public ResponseDto getTotalOrder(@RequestBody TotalOrderModel model) {
        return ResponseDto.of(this.orderService.getAllOrderByStatusAndTime(model.getStatus(), model.getTime_from(), model.getTime_to()), "thống kê tất cả theo trạng thái và thời gian");
    }

    @RolesAllowed("ADMINISTRATOR")
    @Transactional
    @PostMapping("/report/statistic-year-by-time-and-status")
    public ResponseDto getTotalOrderByYear(@RequestBody TotalOrderModel model) {
        return ResponseDto.of(this.orderService.statisticsYearOrderByStatusAndTime(model.getStatus(), model.getTime_from(), model.getTime_to()), "thống kê năm theo trạng thái và thời gian");
    }

    @RolesAllowed("ADMINISTRATOR")
    @Transactional
    @PostMapping("/report/statistic-year-by-time-and-all-status")
    public ResponseDto getTotalOrderByYearAllStatus(@RequestBody TotalOrderModel model) {
        return ResponseDto.of(this.orderService.statisticsYearOrderByAndTime(model.getTime_from(), model.getTime_to()), "thống kê năm theo thời gian");
    }

    @RolesAllowed("ADMINISTRATOR")
    @Transactional
    @PostMapping("/report/statistic-year-select-status")
    public ResponseDto statisticsYearOrderSelectStatus(@RequestBody TotalOrderSelectStatusModel model) {
        return ResponseDto.of(this.orderService.statisticsYearOrderSelectStatus(model.getStatus_orders(),model.getTime_from(), model.getTime_to()), "thống kê năm theo trạng thái");
    }

    @RolesAllowed("ADMINISTRATOR")
    @Transactional
    @PostMapping("/report/total-order-by-time-and-status")
    public ResponseDto getTotalOrderByStatus(@RequestBody TotalOrderModel model) {
        return ResponseDto.of(this.orderService.getTotalOrderByStatusAndTime(model.getStatus(), model.getTime_from(), model.getTime_to()), "thống kê đơn hàng theo trạng thái và thời gian");
    }

    @RolesAllowed("ADMINISTRATOR")
    @Transactional
    @PostMapping("/report/total-user-by-time")
    public ResponseDto getTotalUserByTime(@RequestBody TotalUserModel model) {
        return ResponseDto.of(this.orderService.getTotalUserByTime(model.getTime_from(), model.getTime_to()), "thống kê người dùng theo thời gian");
    }

    @RolesAllowed("ADMINISTRATOR")
    @Transactional
    @GetMapping("/report/get-all-order-groupby-status")
    public ResponseDto getAllOrderGroupByStatus() {
        return ResponseDto.of(this.orderService.getAllOrderGroupByStatus(), "thống kê tất cả đơn hàng theo trạng thái");
    }

}
