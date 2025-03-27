package com.github.yeokyeong_yoon.brand_coordinate_api.controller;

import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Category;
import com.github.yeokyeong_yoon.brand_coordinate_api.dto.CategoryLowestPriceResponse;
import com.github.yeokyeong_yoon.brand_coordinate_api.dto.CategoryPriceResponse;
import com.github.yeokyeong_yoon.brand_coordinate_api.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/lowest-prices")
    public ResponseEntity<Map<String, Object>> getLowestPricesByCategory() {
        log.info("Backend: Received request for GET /categories/lowest-prices");
        try {
            CategoryLowestPriceResponse response = categoryService.findLowestPricesByCategory();
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", response);
            log.info("Backend: Successfully processed request for lowest prices by category");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Backend: Error processing request for lowest prices by category: {}", e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/{category}/price-range")
    public ResponseEntity<Map<String, Object>> getPriceRangeByCategory(@PathVariable Category category) {
        log.info("Backend: Received request for GET /categories/{}/price-range", category);
        try {
            CategoryPriceResponse response = categoryService.findPriceRangeByCategory(category);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", response);
            log.info("Backend: Successfully processed request for price range by category: {}", category);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Backend: Error processing request for price range by category {}: {}", category, e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
} 