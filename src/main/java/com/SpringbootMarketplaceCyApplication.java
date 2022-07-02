package com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.repositories")
public class SpringbootMarketplaceCyApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootMarketplaceCyApplication.class, args);
    }

}
