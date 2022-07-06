package com.services.impl;

import com.dtos.*;
import com.entities.*;
import com.models.OrderModel;
import com.models.SocketNotificationModel;
import com.repositories.*;
import com.services.INotificationService;
import com.services.IOrderService;
import com.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    private INotificationService notificationService;

    @Autowired
    private IUserRepository userRepository;

    @Override
    public List<OrderEntity> findAll() {
        return orderRepository.findAll();
    }

    @Override
    public Page<OrderEntity> findAll(Pageable page) {
        return orderRepository.findAll(page);
    }

    @Override
    public List<OrderEntity> findAll(Specification<OrderEntity> specs) {
        return null;
    }

    @Override
    public Page<OrderEntity> filter(Pageable page, Specification<OrderEntity> specs) {
        return orderRepository.findAll(specs, page);
    }

    @Override
    public OrderEntity findById(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found!!!"));
    }

    //Address,Delete cartDetail
    @Override
    public OrderEntity add(OrderModel model) {
        String uuid = UUID.randomUUID().toString();
        String note = model.getNote();
        AddressEntity address = this.addressRepository.findById(model.getAddressId()).orElseThrow(() -> new RuntimeException("Address not found!!!"));
        String paymentMethod = model.getPaymentMethod().toString();
        List<CartDetailEntity> cartDetailEntities = cartDetailRepository.findAllById(model.getCartDetailIds());
        if (cartDetailEntities.size() == 0) {
            return null;
        }
        List<OrderDetailEntity> orderDetailEntities = new ArrayList<>();
        AtomicReference<Double> totalPrices = new AtomicReference<>(0.0);
        AtomicReference<Integer> totalProducts = new AtomicReference<>(0);


        StringBuilder strAddress = new StringBuilder(address.getStreet());
        strAddress.append(", ").append(address.getWard().getName()).append(", ").append(address.getDistrict().getName()).append(", ").append(address.getProvince().getName());
        OrderEntity order = orderRepository.save(OrderEntity.builder()
                .uuid(uuid)
                .note(note)
                .paymentMethod(paymentMethod)
                .status(EStatusOrder.PENDING.name())
                .mainAddress(strAddress.toString())
                .mainPhone(address.getPhone())
                .mainReceiver(address.getReceiver())
                .deliveryCode(null)
                .addressEntity(address)
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
        order.setTotalPrices(totalPrices.get() + order.getDeliveryFee());
        order.setTotalNumberProducts(totalProducts.get());
        order.setOrderDetails(orderDetailEntities);
        cartRepository.deleteAllByCartDetails_Empty();
        this.notificationService.addForSpecificUser(new SocketNotificationModel(null, "Bạn có đơn hàng mới!, #".concat(order.getUuid()), "", ENotificationCategory.ORDER,OrderEntity.ADMIN_ORDER_URL), this.userRepository.getAllIdsByRole(RoleEntity.ADMINISTRATOR));
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
        return orderRepository.saveAndFlush(order);
    }

    @Override
    public OrderEntity cancelOrder(Long id) {
        OrderEntity order = this.findById(id);
        order.setStatus("CANCEL");
        return orderRepository.saveAndFlush(order);
    }

    @Override
    public OrderEntity onlyUserFindById(Long id, Long userId) {
        return orderRepository.findByIdAndCreatedById(id, userId).orElseThrow(() -> new RuntimeException("Order not found!!!"));
    }

    @Override
    public Page<OrderEntity> onlyUserFindAll(Pageable page, Long userId) {
        return orderRepository.findAllByCreatedById(userId, page);
    }

    @Override
    public OrderEntity findByUUID(String uuid) {
        return orderRepository.findByUuid(uuid).orElseThrow(() -> new RuntimeException("Order not found!!!"));
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
        return orderRepository.saveAndFlush(order);
    }

    @Override
    public List<OrderByStatusAndTimeDto> getAllOrderByStatusAndTime(String status_order,Date time_from,Date time_to) {
        List<Object[]> list = this.orderRepository.findAllByTimeAndStatus(status_order, time_from, time_to);
        List<OrderByStatusAndTimeDto> orderByStatusAndTimeDtoList = new ArrayList<>();

        list.stream().forEach(o -> {
            OrderByStatusAndTimeDto orderByStatusAndTimeDto = new OrderByStatusAndTimeDto();
            orderByStatusAndTimeDto.setHour_in_day((Integer) o[0]);
            orderByStatusAndTimeDto.setTotal_order(o[1] == null ? null : ((BigInteger) o[1]).intValue());
            orderByStatusAndTimeDto.setStatus_order(o[2] == null ? null : (String) o[2]);
            orderByStatusAndTimeDto.setTotal_products(o[3] == null ? null : (Integer) o[3]);
            orderByStatusAndTimeDto.setTotal_prices(o[4] == null ? null : (Double) o[4]);
            orderByStatusAndTimeDto.setOrder_date(o[5] == null ? null : ((Timestamp) o[5]).toLocalDateTime());
            orderByStatusAndTimeDtoList.add(orderByStatusAndTimeDto);
        });

        return orderByStatusAndTimeDtoList;
    }

    @Override
    public List<StatisticsYearByStatusAndTimeDto> statisticsYearOrderByStatusAndTime(String status_order, Date time_from, Date time_to) {
        List<Object[]> list = this.orderRepository.statisticsYearByTimeAndStatus(status_order, time_from, time_to);
        List<StatisticsYearByStatusAndTimeDto> statisticsYearByStatusAndTimeDtos = new ArrayList<>();

        list.stream().forEach(o -> {
            StatisticsYearByStatusAndTimeDto statisticsYearByStatusAndTimeDto = new StatisticsYearByStatusAndTimeDto();
            statisticsYearByStatusAndTimeDto.setMonth_in_year((Integer) o[0]);
            statisticsYearByStatusAndTimeDto.setTotal_order(o[1] == null ? null : ((BigInteger) o[1]).intValue());
            statisticsYearByStatusAndTimeDto.setStatus_order(o[2] == null ? null : (String) o[2]);
            statisticsYearByStatusAndTimeDto.setTotal_products(o[3] == null ? null : ((BigDecimal) o[3]).intValue());
            statisticsYearByStatusAndTimeDto.setTotal_prices(o[4] == null ? null : (Double) o[4]);
            statisticsYearByStatusAndTimeDtos.add(statisticsYearByStatusAndTimeDto);
        });

        return statisticsYearByStatusAndTimeDtos;
    }

    @Override
    public Integer getTotalOrderByStatusAndTime(String status_order, Date time_from, Date time_to) {
        return this.orderRepository.findTotalOrderByTimeAndStatus(status_order,time_from, time_to);
    }

    @Override
    public Double getTotalPriceByStatusAndTime(String status_order, Date time_from, Date time_to) {
        return this.orderRepository.findTotalPriceByTimeAndStatus(status_order,time_from, time_to);
    }

    @Override
    public Integer getTotalUserByTime(Date time_from, Date time_to) {
        return this.orderRepository.findTotalUserByTime(time_from, time_to);
    }

    @Override
    public List<OrderGroupbyStatusDto> getAllOrderGroupByStatus() {
        List<Object[]> list = this.orderRepository.findAllOrderGroupByStatus();
        List<OrderGroupbyStatusDto> listOrderGroupbyStatusDto = new ArrayList<>();

        list.stream().forEach(o -> {
            OrderGroupbyStatusDto orderGroupbyStatusDto = new OrderGroupbyStatusDto();
            orderGroupbyStatusDto.setStatus((String) o[1]);
            orderGroupbyStatusDto.setCount(((BigInteger) o[0]).intValue());
            listOrderGroupbyStatusDto.add(orderGroupbyStatusDto);
        });
        listOrderGroupbyStatusDto.add(new OrderGroupbyStatusDto("list-size", list.stream().mapToInt(o -> ((BigInteger) o[0]).intValue()).sum()));
        return listOrderGroupbyStatusDto;
    }
}

