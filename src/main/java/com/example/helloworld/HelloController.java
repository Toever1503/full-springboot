package com.example.helloworld;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("hello")
    public String hello() {
        return "Welcome to Spring Boot";
    }

    @GetMapping("hello/{name}")
    public String helloName(@PathVariable String name) {
        return "Welcome to Spring Boot, " + name;
    }
}
