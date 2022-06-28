package com.services.impl;

import com.entities.*;
import com.models.OrderModel;
import com.repositories.*;
import com.services.IOrderService;
import com.utils.SecurityUtils;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements IOrderService {
    @Autowired
    IOrderRepository orderRepository;
    @Autowired
    IOrderDetailRepository orderDetailRepository;
    @Autowired
    ICartRepository cartRepository;
    @Autowired
    ICartDetailRepository cartDetailRepository;
    @Autowired
    IAddressRepository addressRepository;
    @Autowired

    @Override
    public List<OrderEntity> findAll() {
        return orderRepository.findAll();
    }

    @Override
    public Page<OrderEntity> findAll(Pageable page) {
        return orderRepository.findAll(page);
    }

    @Override
    public Page<OrderEntity> filter(Pageable page, Specification<OrderEntity> specs) {
        return orderRepository.findAll(specs,page);
    }

    @Override
    public OrderEntity findById(Long id) {
        return orderRepository.findById(id).orElseThrow(()->new RuntimeException("Order not found!!!"));
    }

    //Address,Delete cartDetail
    @Override
    public OrderEntity add(OrderModel model) {
        String uuid = UUID.randomUUID().toString();
        String note = model.getNote();
        AddressEntity address = new AddressEntity();
        String paymentMethod = model.getPaymentMethod().toString();
        List<CartDetailEntity> cartDetailEntities = cartDetailRepository.findAllById(model.getCartDetailIds());
        if(cartDetailEntities.size()==0){
            return null;
        }
        List<CartEntity> cartEntities = cartDetailEntities.stream().map(CartDetailEntity::getCart).collect(Collectors.toList());
        List<OrderDetailEntity> orderDetailEntities = new ArrayList<>();
        AtomicReference<Double> totalPrices = new AtomicReference<>(0.0);
        AtomicReference<Integer> totalProducts = new AtomicReference<>(0);
        OrderEntity order = orderRepository.save(OrderEntity.builder()
                .uuid(uuid)
                .note(note)
                .paymentMethod(paymentMethod)
                .status("PENDING")
                .mainAddress("nope")
                .mainPhone("none")
                .mainReceiver("Hiu")
                .deliveryCode("sadasdassd")
                .deliveryFee(Double.valueOf(30000f))
                .createdBy(SecurityUtils.getCurrentUser().getUser())
                .build());
        cartDetailEntities.stream().forEach(cartDetailEntity -> {
            OrderDetailEntity orderDetailEntity = OrderDetailEntity.builder()
                    .sku(cartDetailEntity.getSku())
                    .quantity(cartDetailEntity.getQuantity())
//                    .option(cartDetailEntity.getSku().getPr())
                    .order(order)
                    .price(cartDetailEntity.getSku().getPrice())
                    .isReview(false)
                    .productId(cartDetailEntity.getSku().getProduct())
                    .build();
            orderDetailEntities.add(orderDetailEntity);
            totalPrices.updateAndGet(v -> v + cartDetailEntity.getSku().getPrice() * cartDetailEntity.getQuantity());
            totalProducts.updateAndGet(v -> v + cartDetailEntity.getQuantity());
        });

        cartDetailRepository.deleteAll(cartDetailEntities);
        orderDetailRepository.saveAll(orderDetailEntities);
        order.setTotalPrices(totalPrices.get()+order.getDeliveryFee());
        order.setTotalNumberProducts(totalProducts.get());
        order.setOrderDetails(orderDetailEntities);
//        cartRepository.deleteAllByCartDetails_Empty();
        return orderRepository.save(order);
    }

    @Override
    public List<OrderEntity> add(List<OrderModel> model) {
        List<OrderEntity> orderEntities = new ArrayList<>();
        model.stream().forEach(orderModel -> {
            this.add(orderModel);
        });
        return orderEntities;
    }

    @Override
    public OrderEntity update(OrderModel model) {
        return null;
    }

    @Override
    public boolean deleteById(Long id) {
        try {
            OrderEntity order = this.findById(id);
            order.setStatus("DELETED");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteByIds(List<Long> ids) {
        ids.stream().forEach(id -> {
            this.deleteById(id);
        });
        return true;
    }

    @Override
    public OrderEntity updateStatusOrder(Long id, String status) {
        OrderEntity order = this.findById(id);
        order.setStatus(status);
        return orderRepository.save(order);
    }

    @Override
    public OrderEntity cancelOrder(Long id) {
        OrderEntity order = this.findById(id);
        order.setStatus("CANCEL");
        return orderRepository.save(order);
    }

    @Override
    public OrderEntity onlyUserFindById(Long id, Long userId) {
        return orderRepository.findByIdAndCreatedById(id, userId).orElseThrow(()->new RuntimeException("Order not found!!!"));
    }

    @Override
    public Page<OrderEntity> onlyUserFindAll(Pageable page, Long userId) {
        return orderRepository.findAllByCreatedById(userId, page);
    }

    @Override
    public OrderEntity findByUUID(String uuid) {
        return orderRepository.findByUuid(uuid).orElseThrow(()->new RuntimeException("Order not found!!!"));
    }

    @Override
    public String getStatusByID(Long id) {
        return this.findById(id).getStatus();
    }

    @Override
    public String getUrlByID(Long id) {
        return this.findById(id).getRedirectUrl();
    }

    @Override
    public OrderEntity updateDeliveryCode(Long id, String deliveryCode) {
        OrderEntity order = this.findById(id);
        order.setDeliveryCode(deliveryCode);
        return orderRepository.save(order);
    }
}
