package com.github.yeokyeong_yoon.brand_coordinate_api.controller;

import com.github.yeokyeong_yoon.brand_coordinate_api.dto.CheapestBrandRequest;
import com.github.yeokyeong_yoon.brand_coordinate_api.dto.CheapestBrandResponse;
import com.github.yeokyeong_yoon.brand_coordinate_api.service.BrandService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/brands")
@CrossOrigin(origins = "*")
public class CustomerBrandController {

    private final BrandService brandService;

    public CustomerBrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> test() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Customer brand controller test endpoint is working!");
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(response);
    }

    @PostMapping("/cheapest")
    public ResponseEntity<Map<String, Object>> getCheapestBrandTotal(@RequestBody CheapestBrandRequest request) {
        log.info("Backend: Received request for POST /brands/cheapest with categories: {}", request.categories());
        try {
            CheapestBrandResponse response = brandService.findCheapestBrandTotal(request.categories());
            log.debug("Backend: Generated response: {}", response);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", response);
            
            log.info("Backend: Successfully processed request for cheapest brand total");
            return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(result);
        } catch (Exception e) {
            log.error("Backend: Error processing request for cheapest brand total: {}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(error);
        }
    }
} 