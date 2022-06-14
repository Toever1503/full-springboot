package com.webs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;


@RestController
@RequestMapping("test-api")
public class TestRestController {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class opt {
        String name;
        String key;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class FormData {
        private Date date;
    }

    @PostMapping
    public FormData getFormData(@RequestBody FormData form) {
        return form;
    }

    public static void main(String[] args) {
        Logger log = LoggerFactory.getLogger(TestRestController.class);
        log.info("Hello World {}", "1241241");
        System.out.printf("Hello World %S", "1241241");
    }

}
