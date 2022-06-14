package com.webs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


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
        private long id;
    }

    @PostMapping
    public FormData getFormData(@RequestPart FormData form, @RequestPart("file")MultipartFile file) {
        return form;
    }

    public static void main(String[] args) {
        Logger log = LoggerFactory.getLogger(TestRestController.class);
        log.info("Hello World {}", "1241241");
        System.out.printf("Hello World %S", "1241241");
    }
}
