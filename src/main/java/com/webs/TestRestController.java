package com.webs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE},
            produces = MediaType.APPLICATION_JSON_VALUE)
    public FormData getFormData(@RequestPart FormData form, @RequestPart("file")MultipartFile file) {
        return form;
    }
}
