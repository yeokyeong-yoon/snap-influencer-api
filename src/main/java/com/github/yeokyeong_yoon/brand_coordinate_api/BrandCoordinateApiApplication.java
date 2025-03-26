package com.github.yeokyeong_yoon.brand_coordinate_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class BrandCoordinateApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(BrandCoordinateApiApplication.class, args);
    }

} 