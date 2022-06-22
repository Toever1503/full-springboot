package com.webs;

import com.dtos.ResponseDto;
import com.services.VnPayService;
import org.joda.time.DateTime;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/transaction")
public class PaymentResources {
    final VnPayService vnPayService;

    public PaymentResources(VnPayService vnPayService) {
        this.vnPayService = vnPayService;
    }

    @PostMapping("/checkout")
    public ResponseDto sendPayRequest(HttpServletRequest request, @RequestParam("id") Long id, @RequestParam("url") String url){
        try {
            return ResponseDto.of(vnPayService.PerformTransaction(id,request,url),"Send pay request");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    @GetMapping("/result")
    public ResponseDto getPayResult(HttpServletRequest request) throws UnsupportedEncodingException {
        System.out.println(DateTime.now());
        return ResponseDto.of(vnPayService.getTransactionResult(request),"Get Transaction Information");
    }


}
