package com.sheikh.orderinventorysystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class OrderInventorySystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderInventorySystemApplication.class, args);
    }

}
