package com.services;

import com.dtos.EPaymentMethod;
import com.dtos.EStatusOrder;
import com.dtos.PaymentResultDto;
import com.entities.OrderEntity;
import com.repositories.IOrderRepository;
import com.utils.SecurityUtils;
import com.utils.VnPayUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class VnPayService {
    final IOrderService orderService;
    final IOrderRepository orderRepository;
    final String OLD_FORMAT = "yyyyMMddHHmmss";
    final String NEW_FORMAT = "yyyy/MM/dd'T'HH:mm:ss";
    final String ALLOWED_STATUS[] = {"PENDING","APPROVE","PAYING","FAILED"};
    public VnPayService(IOrderService orderService, IOrderRepository orderRepository) {
        this.orderService = orderService;
        this.orderRepository = orderRepository;
    }

    public String PerformTransaction(Long id, HttpServletRequest request, String url) throws UnsupportedEncodingException {
        OrderEntity curOrder = orderRepository.findById(id).orElseThrow(()-> new RuntimeException("Not Found"));
        if(curOrder.getCreatedBy().getId()== SecurityUtils.getCurrentUserId()&& Arrays.stream(ALLOWED_STATUS).filter(s -> s.equals(curOrder.getStatus())).collect(Collectors.toList()).size()>0){
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        if(curOrder.getPaymentMethod().equals(EPaymentMethod.CASH.toString()))
            curOrder.setPaymentMethod(EPaymentMethod.BANK.toString());
        SimpleDateFormat formatter = new SimpleDateFormat(OLD_FORMAT);
        String vnp_CreateDate = formatter.format(cld.getTime());
        //Expire time
        cld.add(Calendar.MINUTE,15);
        String vnp_ExpireDate = formatter.format(cld.getTime());

        Map<String,String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version",VnPayUtils.vnp_Version);
        vnp_Params.put("vnp_Command",VnPayUtils.vnp_Command);
        vnp_Params.put("vnp_TmnCode",VnPayUtils.vnp_TmnCode);
        vnp_Params.put("vnp_Amount",String.valueOf(curOrder.getTotalPrices().intValue()*100));
        vnp_Params.put("vnp_BankCode", VnPayUtils.vnp_BankCode);
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
        vnp_Params.put("vnp_CurrCode",VnPayUtils.vnp_CurrCode);
        vnp_Params.put("vnp_IpAddr", VnPayUtils.getIpAddress(request));
        vnp_Params.put("vnp_Locale",VnPayUtils.vnp_Locale);
        vnp_Params.put("vnp_OrderInfo",curOrder.getNote());
        vnp_Params.put("vnp_OrderType",VnPayUtils.vnp_OrderType);
        vnp_Params.put("vnp_ReturnUrl", VnPayUtils.vnp_ResUrl);
        vnp_Params.put("vnp_TxnRef",curOrder.getUuid());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List fieldList = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldList);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        Iterator itr =  fieldList.iterator();
        while (itr.hasNext()){
            String fieldName = (String) itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if(fieldValue!=null && (fieldValue.length()>0)){
                hashData.append(fieldName);
                hashData.append("=");
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));

                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append("=");
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));

                if(itr.hasNext()){
                    query.append("&");
                    hashData.append("&");
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = VnPayUtils.hmacSHA512(VnPayUtils.vnp_HashSecret,hashData.toString());
        curOrder.setStatus(EStatusOrder.PAYING.toString());
        curOrder.setRedirectUrl(url);
        orderRepository.save(curOrder);
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = VnPayUtils.vnp_Url + "?" + queryUrl;
        return paymentUrl;
        }
        else
        return null;
    }
    public PaymentResultDto getTransactionResult(HttpServletRequest request) throws UnsupportedEncodingException {
        try {

        /*  IPN URL: Record payment results from VNPAY
        Implementation steps:
        Check checksum
        Find transactions (vnp_TxnRef) in the database (checkOrderId)
        Check the payment status of transactions before updating (checkOrderStatus)
        Check the amount (vnp_Amount) of transactions before updating (checkAmount)
        Update results to Database
        Return recorded results to VNPAY
        */

            // ex:  	PaymnentStatus = 0; pending
            //              PaymnentStatus = 1; success
            //              PaymnentStatus = 2; Faile

            //Begin process return from VNPAY
            Map fields = new HashMap();
            for (Enumeration params = request.getParameterNames(); params.hasMoreElements(); ) {
                String fieldName = URLEncoder.encode((String) params.nextElement(), StandardCharsets.US_ASCII.toString());
                String fieldValue = URLEncoder.encode(request.getParameter(fieldName), StandardCharsets.US_ASCII.toString());
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    fields.put(fieldName, fieldValue);
                }
            }

            String vnp_SecureHash = request.getParameter("vnp_SecureHash");
            if (fields.containsKey("vnp_SecureHashType")) {
                fields.remove("vnp_SecureHashType");
            }
            if (fields.containsKey("vnp_SecureHash")) {
                fields.remove("vnp_SecureHash");
            }

            // Check checksum
            String signValue = VnPayUtils.hashAllFields(fields);

            if (signValue.equals(vnp_SecureHash)) {
                OrderEntity order = new OrderEntity();
                boolean checkOrderId;
                if (orderRepository.findByUuid(String.valueOf(fields.get("vnp_TxnRef"))).isPresent()){
                    order = orderRepository.findByUuid(String.valueOf(fields.get("vnp_TxnRef"))).get();
                    if(order.getTransactionNo()==null)
                        checkOrderId = true;
                    else
                        checkOrderId = false;
                }
                else
                    checkOrderId = false;
                // vnp_TxnRef exists in your database
                boolean checkAmount;
                if(order.getTotalPrices().intValue() == Integer.parseInt(String.valueOf(fields.get("vnp_Amount")))/100){
                    checkAmount = true;
                }else
                {
                    checkAmount = false;
                }
                // vnp_Amount is valid (Check vnp_Amount VNPAY returns compared to the
//                amount of the code (vnp_TxnRef) in the Your database)
                boolean checkOrderStatus; // PaymnentStatus = 0 (pending)
                OrderEntity finalOrder = order;
                if(Arrays.stream(ALLOWED_STATUS).anyMatch(s -> s.equals(finalOrder.getStatus()))){
                    checkOrderStatus = true;
                }
                else {
                    checkOrderStatus = false;
                }
                if (checkOrderId) {
                    if (checkAmount) {
                        if (checkOrderStatus) {
                            if ("00".equals(request.getParameter("vnp_ResponseCode"))) {
                                PaymentResultDto resultDto = new PaymentResultDto();
                                resultDto.setAmount(Long.valueOf(String.valueOf(fields.get("vnp_Amount"))));
                                resultDto.setBankCode(String.valueOf(fields.get("vnp_BankCode")));
                                resultDto.setBankTransNo(String.valueOf(fields.get("vnp_BankTranNo")));
                                StringBuilder sb = new StringBuilder();
                                String originalDate = String.valueOf(fields.get("vnp_PayDate"));
                                sb.append(originalDate);
                                for (int i=1;i<=5;i++){
                                    if(i==1){
                                        sb.insert(4,"/");
                                    }else if(i<3 && i!=1){
                                        sb.insert(i*2+3,"/");
                                    }
                                    else if(i==3){
                                        sb.insert(i*2+4," ");
                                    }else {
                                        sb.insert(i*3+1,":");
                                    }
                                }
                                resultDto.setPayDate(sb.toString());
                                resultDto.setOrderInfo(String.valueOf(fields.get("vnp_OrderInfo")).replace("+"," "));
                                resultDto.setTransactionNo(String.valueOf(fields.get("vnp_TransactionNo")));
//                                resultDto.set(String.valueOf(fields.get("vnp_TransactionNo")));
                                resultDto.setStatus("SUCCESS");
                                order.setTransactionNo(String.valueOf(fields.get("vnp_TransactionNo")));
                                order.setStatus(EStatusOrder.PAID.toString());
                                orderRepository.save(order);
                                System.out.print("{\"RspCode\":\"00\",\"Message\":\"Confirm Success\"}");
                                return resultDto;
                                //Update DB When success
                            } else {
                                orderRepository.changeOrderStatusByID(EStatusOrder.FAILED.toString(), order.getId());
                                System.out.print("{\"RspCode\":\"00\",\"Message\":\"Confirm Success\"}");
                                return null;
                            }
                        } else {
                            System.out.print("{\"RspCode\":\"02\",\"Message\":\"Order already confirmed\"}");
                            orderRepository.changeOrderStatusByID(EStatusOrder.FAILED.toString(), order.getId());
                            return null;
                        }
                    } else {
                        System.out.print("{\"RspCode\":\"04\",\"Message\":\"Invalid Amount\"}");
                        orderRepository.changeOrderStatusByID(EStatusOrder.FAILED.toString(), order.getId());
                        return null;
                    }
                } else {
                    System.out.print("{\"RspCode\":\"01\",\"Message\":\"Order not Found\"}");
                    orderRepository.changeOrderStatusByID(EStatusOrder.FAILED.toString(), order.getId());
                    return null;
                }
            } else {
                System.out.print("{\"RspCode\":\"97\",\"Message\":\"Invalid Checksum\"}");
                return null;
            }
        } catch (Exception e) {
            System.out.print("{\"RspCode\":\"99\",\"Message\":\"Unknow error\"}");
        }
        return null;
    }
}
