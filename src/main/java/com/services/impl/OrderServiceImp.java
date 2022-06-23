package com.services.impl;

import com.dtos.EStatusOrder;
import com.entities.*;
import com.models.OrderModel;
import com.models.SocketNotificationModel;
import com.repositories.IOptionsRepository;
import com.repositories.IOrderDetailRepository;
import com.repositories.IOrderRepository;
import com.services.*;
import com.utils.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImp implements IOrderService {
    private final IOrderRepository orderRepository;
    private final IOrderDetailRepository orderDetailRepository;
    private final ICartService cartService;
    private final IOptionsRepository optionsRepository;
    private final IAddressService addressService;
    private final ISocketService socketService; // remove this line later
    private final INotificationService notificationService;


    public OrderServiceImp(IOrderRepository orderRepository, IOrderDetailRepository orderDetailRepository, ICartService cartService, IOptionsRepository optionsRepository, IAddressService addressService, ISocketService socketService, INotificationService notificationService) {
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.cartService = cartService;
        this.optionsRepository = optionsRepository;
        this.addressService = addressService;
        this.socketService = socketService;
        this.notificationService = notificationService;
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
                OptionEntity optionId = this.optionsRepository.findById(cart.getOptionId()).get();
                if (cart != null) {
                    OrderDetailEntity orderDetailEntity = OrderDetailEntity.builder()
                            .productId(cart.getProduct())
                            .optionId(optionId.getOptionName())
                            .price(optionId.getNewPrice())
                            .quantity(cart.getQuantity())
                            .isReview(false)
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
        totalPrices += orderEntity.getDeliveryFee();
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
//        socketService.sendOrderNotificationForSingleUser(orderEntity,orderEntity.getCreatedBy().getId(),"abcd.com","Don hang da duoc tao: ");
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
//        socketService.sendOrderNotificationForSingleUser(orderOrigin,orderOrigin.getCreatedBy().getId(),"abcd.com", "Don hang da duoc cap nhat: ");
        return this.orderRepository.save(orderOrigin);
    }

    @Override
    public OrderEntity cancelOrder(Long id) {
        OrderEntity orderOrigin = this.findById(id);
        if (orderOrigin.getStatus().equals(EStatusOrder.PENDING.toString())) {
            orderOrigin.setStatus(EStatusOrder.CANCELED.name());
//            socketService.sendOrderNotificationForSingleUser(orderOrigin,orderOrigin.getCreatedBy().getId(),"abcd.com", "Don hang da bi huy");
            return this.orderRepository.save(orderOrigin);
        } else if (orderOrigin.getStatus().equals(EStatusOrder.PAID.toString())) {
            orderOrigin.setStatus(EStatusOrder.REFUNDING.name());
//            socketService.sendOrderNotificationForSingleUser(orderOrigin,orderOrigin.getCreatedBy().getId(),"abcd.com", "Don hang da bi huy");
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

    @Override
    public OrderEntity updateDeliveryCode(Long id, String deliveryCode) {
        OrderEntity orderEntity = this.findById(id);
        orderEntity.setDeliveryCode(deliveryCode);
        orderEntity.setStatus(EStatusOrder.DELIVERING.name());
        this.notificationService.addForSpecificUser(new SocketNotificationModel(null, "Don hang ".concat(orderEntity.getUuid().concat("da duoc cap nhat ma van chuyen!")), "", OrderEntity.ORDER_DETAIL_URL), List.of(orderEntity.getCreatedBy().getId()));
        return this.orderRepository.save(orderEntity);
    }
}
