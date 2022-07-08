package com.config;

import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.TimeZone;

@Configuration
public class DateTimeConfig {
    @PostConstruct
    public void init() {
        //TimeZone.setDefault(TimeZone.getTimeZone("UTC+07:00"));
        System.out.println("Date in UTC: " + new Date().toString());
    }
}
