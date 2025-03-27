package com.github.yeokyeong_yoon.brand_coordinate_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import java.util.Map;

@SpringBootApplication
@EnableJpaRepositories
@RestController
public class BrandCoordinateApiApplication {

    public static void main(String[] args) {
        System.out.println("Starting application...");
        System.setProperty("logging.level.org.springframework", "DEBUG");
        SpringApplication.run(BrandCoordinateApiApplication.class, args);
        System.out.println("Application started!");
    }
} 