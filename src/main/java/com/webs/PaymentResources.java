package com.webs;

import com.config.FrontendConfiguration;
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
            return ResponseDto.of(vnPayService.PerformTransaction(id,request,url),"Gửi yêu cầu thanh toán");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    @Transactional
    @GetMapping("/result")
    public ResponseDto getPayResult(HttpServletRequest request, HttpServletResponse response) throws IOException {
            System.out.println(DateTime.now());
            PaymentResultDto dto = vnPayService.getTransactionResult(request, response);
            if(dto==null){
                vnPayService.cancelOrderByUUID(request.getParameter(String.valueOf(String.valueOf(request.getParameter("vnp_TxnRef")))));
                response.sendRedirect(FrontendConfiguration.ORDER_CANCEL_URL);
                return ResponseDto.of(null,"Hủy thanh toán");
            }
        response.sendRedirect(dto.getUrl()+"?Ammount="+dto.getAmount()+"&BankCode="+dto.getBankCode()+"&Transaction="+dto.getTransactionNo()+"&PayDate="+dto.getPayDate()+"&Info="+String.valueOf(dto.getOrderInfo().replace(" ","+"))+"&Status="+dto.getStatus());
        return ResponseDto.of(dto,"Lấy kết quả thanh toán");
//        response.sendRedirect(order.getRedirectUrl()+"&BankCode="+String.valueOf(fields.get("vnp_BankCode"))+"&Transaction="+order.getTransactionNo()+"&Status="+order.getStatus());

    }


}
