package com.services.impl;

import com.dtos.EStatusOrder;
import com.entities.Address;
import com.entities.CartEntity;
import com.entities.OrderDetailEntity;
import com.entities.OrderEntity;
import com.models.OrderModel;
import com.repositories.IOptionsRepository;
import com.repositories.IOrderDetailRepository;
import com.repositories.IOrderRepository;
import com.services.IAddressService;
import com.services.ICartService;
import com.services.IOrderService;
import com.utils.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImp implements IOrderService {
    private final IOrderRepository orderRepository;
    private final IOrderDetailRepository orderDetailRepository;
    private final ICartService cartService;
    private final IOptionsRepository optionsRepository;
    private final IAddressService addressService;

    public OrderServiceImp(IOrderRepository orderRepository, IOrderDetailRepository orderDetailRepository, ICartService cartService, IOptionsRepository optionsRepository, IAddressService addressService) {
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.cartService = cartService;
        this.optionsRepository = optionsRepository;
        this.addressService = addressService;
    }

    @Override
    public List<OrderEntity> findAll() {
        return null;
    }

    @Override
    public Page<OrderEntity> findAll(Pageable page) {
        return this.orderRepository.findAll(page);
    }

    @Override
    public Page<OrderEntity> filter(Pageable page, Specification<OrderEntity> specs) {
        return this.orderRepository.findAll(specs, page);
    }

    @Override
    public OrderEntity findById(Long id) {
        return this.orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found, id: " + id));
    }

    @Override
    public OrderEntity add(OrderModel model) {
        OrderEntity orderEntity = OrderModel.toEntity(model);
        // khi nguoi dung chon san pham va thanh toan thi se luu list san pham chon vao order detail, sau do se xoa nhung san pham da luu vao order detail
        List<OrderDetailEntity> orderDetailEntities = new ArrayList<>();
        if (model.getOrderDetailIds() != null) {
            model.getOrderDetailIds().stream().forEach(orderDetailId -> {

                CartEntity cart = this.cartService.findById(orderDetailId);
                if (cart != null) {
                    OrderDetailEntity orderDetailEntity = OrderDetailEntity.builder()
                            .productId(cart.getProduct().getId())
                            .optionId(cart.getOptionId())
                            .price(this.optionsRepository.findById(cart.getOptionId()).get().getNewPrice())
                            .quantity(cart.getQuantity())
                            .build();
                    orderDetailEntity = this.orderDetailRepository.save(orderDetailEntity);
                    orderDetailEntities.add(orderDetailEntity);
                    this.cartService.deleteById(cart.getId());
                }
            });
        } else {
            new RuntimeException("List product is null, please select product");
        }

        Double totalPrices = 0.0;
        Integer totalNumberProducts = 0;

        for (OrderDetailEntity orderDetailEntity : orderDetailEntities) {
            totalNumberProducts += orderDetailEntity.getQuantity();
            totalPrices += orderDetailEntity.getPrice() * orderDetailEntity.getQuantity();
        }
        // set address user
        Address address = this.addressService.findById(model.getAddressId());
        orderEntity.setAddress(address);

        StringBuilder strAddress = new StringBuilder(address.getStreet());
        strAddress.append(", ").append(address.getWard().getName()).append(", ").append(address.getDistrict().getName()).append(", ").append(address.getProvince().getName());
        orderEntity.setMainAddress(strAddress.toString());
        orderEntity.setMainPhone(address.getPhone());
        orderEntity.setMainReceiver(address.getReceiver());

        orderEntity.setTotalNumberProducts(totalNumberProducts);
        orderEntity.setTotalPrices(totalPrices);
        orderEntity.setOrderDetails(orderDetailEntities);
        orderEntity.setCreatedBy(SecurityUtils.getCurrentUser().getUser());
        return this.orderRepository.save(orderEntity);
    }

    @Override
    public List<OrderEntity> add(List<OrderModel> model) {
        return null;
    }

    @Override
    public OrderEntity update(OrderModel model) {
        return null;
    }

    @Override
    public boolean deleteById(Long id) {
        return false;
    }

    @Override
    public boolean deleteByIds(List<Long> ids) {
        return false;
    }

    @Override
    public OrderEntity updateStatusOrder(Long id, String status) {
        OrderEntity orderOrigin = this.findById(id);
        orderOrigin.setStatus(status);
        return this.orderRepository.save(orderOrigin);
    }

    @Override
    public OrderEntity cancelOrder(Long id) {
        OrderEntity orderOrigin = this.findById(id);
        if (orderOrigin.getStatus().equals(EStatusOrder.PENDING.toString())) {
            orderOrigin.setStatus(EStatusOrder.CANCELED.name());
            return this.orderRepository.save(orderOrigin);
        } else if (orderOrigin.getStatus().equals(EStatusOrder.PAID.toString())) {
            orderOrigin.setStatus(EStatusOrder.REFUNDING.name());
            return this.orderRepository.save(orderOrigin);
        }
        throw new RuntimeException("Order can't cancel, id: " + id);
    }

    @Override
    public OrderEntity onlyUserFindById(Long id, Long userId) {
        return this.orderRepository.findByIdAndCreatedById(id, userId).orElseThrow(() -> new RuntimeException("User id: " + userId + ", Order not found, id: " + id));
    }

    @Override
    public Page<OrderEntity> onlyUserFindAll(Pageable page, Long userId) {
        return this.orderRepository.findAllByCreatedById(userId, page);
    }

    public OrderEntity findByUUID(String uuid) {
        return orderRepository.findByUuid(uuid).orElseThrow(() -> new RuntimeException("Not Found!"));
    }

    @Override
    public String getStatusByID(Long id) {
        return orderRepository.getStatusByID(id, SecurityUtils.getCurrentUserId()).orElseThrow(() -> new RuntimeException("Order not found!!!"));
    }

    @Override
    public String getUrlByID(Long id) {
        return orderRepository.getUrlByID(id, SecurityUtils.getCurrentUserId()).orElseThrow(() -> new RuntimeException("Order not found!!!"));
    }
}
