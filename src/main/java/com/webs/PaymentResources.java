package com.webs;

import com.dtos.ResponseDto;
import com.models.PayModel;
import com.services.VnPayService;
import org.joda.time.DateTime;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

@RestController
@RequestMapping("/transaction")
public class PaymentResources {
    final VnPayService vnPayService;

    public PaymentResources(VnPayService vnPayService) {
        this.vnPayService = vnPayService;
    }

    @PostMapping("/checkout")
    public ResponseDto sendPayRequest(@RequestBody @Valid PayModel payModel, HttpServletRequest request){
        try {
            return ResponseDto.of(vnPayService.PerformTransaction(payModel,request),"Send pay request");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    @GetMapping("/payment")
    public ResponseDto getPayResult(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        System.out.println(DateTime.now());
        return ResponseDto.of(vnPayService.getPaymentUrl(response,request),"Get Transaction Information");
    }


}
