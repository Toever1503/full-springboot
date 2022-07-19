package com.services.impl;

import com.config.FrontendConfiguration;
import com.dtos.*;
import com.entities.*;
import com.models.OrderModel;
import com.models.SocketNotificationModel;
import com.repositories.*;
import com.services.INotificationService;
import com.services.IOrderService;
import com.services.IProductService;
import com.services.MailService;
import com.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

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
    MailService mailService;

    @Autowired
    private IProductService productService;

    @Autowired
    private final IProductRepository productRepository;
    @Autowired
    private INotificationService notificationService;

    @Autowired
    private IUserRepository userRepository;

    public OrderServiceImpl(IProductRepository productRepository) {
        this.productRepository = productRepository;
    }

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
        if (!model.getCartDetailIds().stream().findFirst().isPresent()) {
            throw new RuntimeException("Cart is empty!!!");
        }
        model.getCartDetailIds().stream().forEach(x -> {
            CartEntity cart = cartRepository.findCartByCartDetails_Id(x).orElseThrow(() -> new RuntimeException("Cart not found!!!"));
            if (!cart.getUser().getId().equals(SecurityUtils.getCurrentUserId())) {
                throw new RuntimeException("Cart item not valid!!!");
            }
        });
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


        List<ProductEntity> productEntities = new ArrayList<>();

        cartDetailEntities.stream().forEach(cartDetailEntity -> {
            ProductSkuEntity productSku = cartDetailEntity.getSku();
            ProductEntity product = productSku.getProduct(); // for get product

            OrderDetailEntity orderDetailEntity = OrderDetailEntity.builder()
                    .sku(productSku)
                    .quantity(cartDetailEntity.getQuantity())
                    .option(productSku.getOptionName())
                    .order(order)
                    .price(productSku.getPrice())
                    .isReview(false)
                    .productId(product)
                    .build();
            // change quantity of product
            int remainQuantity = productSku.getInventoryQuantity() - cartDetailEntity.getQuantity();
            productSku.setInventoryQuantity(remainQuantity < 0 ? 0 : remainQuantity);

            // change total sold
            product.setTotalSold(product.getTotalSold() + cartDetailEntity.getQuantity());

            // add to list to save later
            productEntities.add(product);

            orderDetailEntities.add(orderDetailEntity);
            totalPrices.updateAndGet(v -> v + cartDetailEntity.getSku().getPrice() * cartDetailEntity.getQuantity());
            totalProducts.updateAndGet(v -> v + cartDetailEntity.getQuantity());

        });

        try {
            // save product again
            productRepository.saveAll(productEntities);

            cartDetailRepository.deleteAll(cartDetailEntities);
            orderDetailRepository.saveAll(orderDetailEntities);
            order.setTotalPrices(totalPrices.get() + order.getDeliveryFee());
            order.setTotalNumberProducts(totalProducts.get());
            order.setOrderDetails(orderDetailEntities);
            cartRepository.deleteAllByCartDetails_Empty();
            orderRepository.saveAndFlush(order);

            this.notificationService.addForSpecificUser(
                    new SocketNotificationModel(null,
                            "Bạn có đơn hàng mới!, #".concat(order.getUuid()),
                            "", ENotificationCategory.ORDER,
                            FrontendConfiguration.ADMIN_ORDER_DETAIL_URL + order.getId()),
                    this.userRepository.getAllIdsByRole(RoleEntity.ADMINISTRATOR));
            this.mailUser("http://15.164.227.244/", order);
            return order;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error when save order!!!");
        } finally {
            productEntities.forEach(productService::saveDtoOnElasticsearch);
        }
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
        order.setStatus("CANCELED");
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
    public List<OrderByStatusAndTimeDto> getAllOrderByStatusAndTime(String status_order, Date time_from, Date time_to) {
        List<Object[]> list = this.orderRepository.findAllByTimeAndStatus(status_order, time_from, time_to);
        List<OrderByStatusAndTimeDto> orderByStatusAndTimeDtoList = new ArrayList<>();

        list.stream().forEach(o -> {
            OrderByStatusAndTimeDto orderByStatusAndTimeDto = new OrderByStatusAndTimeDto();
            orderByStatusAndTimeDto.setHour_in_day((Integer) o[0]);
            orderByStatusAndTimeDto.setTotal_order(o[1] == null ? 0 : ((BigInteger) o[1]).intValue());
            orderByStatusAndTimeDto.setStatus_order(o[2] == null ? null : (String) o[2]);
            orderByStatusAndTimeDto.setTotal_products(o[3] == null ? 0 : (Integer) o[3]);
            orderByStatusAndTimeDto.setTotal_prices(o[4] == null ? 0 : (Double) o[4]);
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
            statisticsYearByStatusAndTimeDto.setTotal_order(o[1] == null ? 0 : ((BigInteger) o[1]).intValue());
            statisticsYearByStatusAndTimeDto.setStatus_order(o[2] == null ? null : (String) o[2]);
            statisticsYearByStatusAndTimeDto.setTotal_products(o[3] == null ? 0 : ((BigDecimal) o[3]).intValue());
            statisticsYearByStatusAndTimeDto.setTotal_prices(o[4] == null ? 0 : (Double) o[4]);
            statisticsYearByStatusAndTimeDtos.add(statisticsYearByStatusAndTimeDto);
        });

        return statisticsYearByStatusAndTimeDtos;
    }

    @Override
    public Map<Object, List<StatisticsYearByStatusAndTimeDto>> statisticsYearOrderByAndTime(Date time_from, Date time_to) {
        Map<Object, List<StatisticsYearByStatusAndTimeDto>> map = new HashMap<>();
        List<String> status_orders = List.of("APPROVE", "PAID", "REFUNDED", "COMPLETED", "CANCELED");
        status_orders.stream().forEach(status_order -> {
            map.put(status_order, this.statisticsYearOrderByStatusAndTime(status_order, time_from, time_to));
        });
        return map;
    }

    @Override
    public Map<Object, List<StatisticsYearByStatusAndTimeDto>> statisticsYearOrderSelectStatus(List<String> status_orders, Date time_from, Date time_to) {
        Map<Object, List<StatisticsYearByStatusAndTimeDto>> map = new HashMap<>();
        status_orders.stream().forEach(status_order -> {
            map.put(status_order, this.statisticsYearOrderByStatusAndTime(status_order, time_from, time_to));
        });
        return map;
    }

    @Override
    public List<TotalOrderWeekAndMonthByStatusAndTimeDto> getTotalOrderByStatusAndTime(String status_order, Date time_from, Date time_to) {
        List<Object[]> list = this.orderRepository.findTotalOrderByTimeAndStatus(status_order, time_from, time_to);
        List<TotalOrderWeekAndMonthByStatusAndTimeDto> totalOrderWeekAndMonthByStatusAndTimeDtos = new ArrayList<>();

        list.stream().forEach(o -> {
            TotalOrderWeekAndMonthByStatusAndTimeDto totalOrderWeekAndMonthByStatusAndTimeDto = new TotalOrderWeekAndMonthByStatusAndTimeDto();
            totalOrderWeekAndMonthByStatusAndTimeDto.setTotal_order(o[0] == null ? 0 : ((BigInteger) o[0]).intValue());
            totalOrderWeekAndMonthByStatusAndTimeDto.setStatus_order(o[1] == null ? null : (String) o[1]);
            totalOrderWeekAndMonthByStatusAndTimeDto.setTotal_products(o[3] == null ? 0 : ((BigDecimal) o[3]).intValue());
            totalOrderWeekAndMonthByStatusAndTimeDto.setTotal_prices(o[2] == null ? 0 : (Double) o[2]);
            totalOrderWeekAndMonthByStatusAndTimeDtos.add(totalOrderWeekAndMonthByStatusAndTimeDto);
        });

        return totalOrderWeekAndMonthByStatusAndTimeDtos;
    }


    @Override
    public List<StatisticsUserDto> getTotalUserByTime(Date time_from, Date time_to) {
        List<Object[]> list = this.orderRepository.findTotalUserByTime(time_from, time_to);
        List<StatisticsUserDto> statisticsUserDtos = new ArrayList<>();
        list.stream().forEach(o -> {
            StatisticsUserDto statisticsUserDto = new StatisticsUserDto();
            statisticsUserDto.setMonth_in_year((Integer) o[0]);
            statisticsUserDto.setTotal_user(o == null ? 0 : ((BigInteger) o[1]).intValue());
            statisticsUserDtos.add(statisticsUserDto);
        });
        return statisticsUserDtos;
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

    private void mailUser(String url, OrderEntity order) {
        new Thread("Send Notify Order Mail") {
            @Override
            public void run() {
                Map<String, Object> context = new HashMap<>();
                context.put("url", url);
                context.put("order", order);
                DecimalFormat format = new DecimalFormat("₫#,###");
                context.put("formatter", format);
                context.put("totalPrices", order.getTotalPrices());
                context.put("deliveryFee", order.getDeliveryFee());
                try {
                    mailService.sendMail("OrderDetailMailTemplate", order.getCreatedBy().getEmail(), "Đơn hàng đã được đặt", context);
                } catch (MessagingException e) {
                    throw new RuntimeException(e);
                }
            }
        }.start();
    }
}
