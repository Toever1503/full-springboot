package com.webs;

import com.dtos.PaymentResultDto;
import com.dtos.ResponseDto;
import com.services.VnPayService;
import org.joda.time.DateTime;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.logging.Logger;

@RestController
@RequestMapping("/transaction")
public class PaymentResources {
    final VnPayService vnPayService;

    public PaymentResources(VnPayService vnPayService) {
        this.vnPayService = vnPayService;
    }

    @Transactional
    @PostMapping("/checkout")
    public ResponseDto sendPayRequest(HttpServletRequest request, @RequestParam("id") Long id, @RequestParam("url") String url){
        try {
            return ResponseDto.of(vnPayService.PerformTransaction(id,request,url),"Send pay request");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    @Transactional
    @GetMapping("/result")
    public ResponseDto getPayResult(HttpServletRequest request, HttpServletResponse response) throws IOException {
            System.out.println(DateTime.now());
            PaymentResultDto dto = vnPayService.getTransactionResult(request, response);
        response.sendRedirect("http://192.168.1.30:8080/order-success"+"?Ammount="+dto.getAmount()+"&BankCode="+dto.getBankCode()+"&Transaction="+dto.getTransactionNo()+"&PayDate="+dto.getPayDate()+"&Info="+String.valueOf(dto.getOrderInfo().replace(" ","+"))+"&Status="+dto.getStatus());
        return ResponseDto.of(dto,"Get Transaction Information");
//        response.sendRedirect(order.getRedirectUrl()+"&BankCode="+String.valueOf(fields.get("vnp_BankCode"))+"&Transaction="+order.getTransactionNo()+"&Status="+order.getStatus());

    }


}
