package com.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class SchedulingConfiguration {
    @Bean
    @ConditionalOnProperty(value = "job.enabled", matchIfMissing =true, havingValue = "true")
    public ScheduledJobConfig scheduledJob(){
        return new ScheduledJobConfig();
    }
}
