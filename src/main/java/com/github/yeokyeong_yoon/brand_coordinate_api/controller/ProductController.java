package com.github.yeokyeong_yoon.brand_coordinate_api.controller;

import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Category;
import com.github.yeokyeong_yoon.brand_coordinate_api.dto.CategoryPriceResponse;
import com.github.yeokyeong_yoon.brand_coordinate_api.dto.CheapestBrandResponse;
import com.github.yeokyeong_yoon.brand_coordinate_api.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/products")
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/lowest-prices")
    public ResponseEntity<Map<String, Object>> getLowestPricesByCategory() {
        log.info("Received request to get lowest prices by category");
        try {
            log.debug("Calling productService.findLowestPricesByCategory()");
            var result = productService.findLowestPricesByCategory();
            log.debug("Received response from service: {}", result);

            Map<String, Object> response = Map.of(
                    "success", true,
                    "data", result
            );

            log.info("Successfully retrieved lowest prices. Returning response: {}", response);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (Exception e) {
            log.error("Failed to get lowest prices", e);
            Map<String, Object> error = Map.of(
                    "success", false,
                    "message", e.getMessage()
            );
            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(error);
        }
    }

    @PostMapping("/cheapest-brand")
    public ResponseEntity<Map<String, Object>> getCheapestBrandTotal(
            @RequestBody Map<String, List<Category>> request) {
        log.info("Received request to get cheapest brand total. Request body: {}", request);
        try {
            List<Category> categories = request.get("categories");
            if (categories == null || categories.isEmpty()) {
                log.warn("No categories provided in request");
                throw new IllegalArgumentException("Categories list cannot be empty");
            }

            log.debug("Calling productService.findCheapestBrandTotal() with categories: {}",
                    categories);
            var result = productService.findCheapestBrandTotal(categories);
            log.debug("Received response from service: {}", result);

            Map<String, Object> response = Map.of(
                    "success", true,
                    "data", result
            );

            log.info("Successfully retrieved cheapest brand total. Returning response: {}",
                    response);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (Exception e) {
            log.error("Failed to get cheapest brand total", e);
            Map<String, Object> error = Map.of(
                    "success", false,
                    "message", e.getMessage()
            );
            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(error);
        }
    }

    @GetMapping("/categories/{category}/price-range")
    public ResponseEntity<Map<String, Object>> getPriceRangeByCategory(
            @PathVariable Category category) {
        log.info("Received request to get price range for category: {}", category);
        try {
            log.debug("Calling productService.findPriceRangeByCategory() with category: {}",
                    category);
            var result = productService.findPriceRangeByCategory(category);
            log.debug("Received response from service: {}", result);

            Map<String, Object> response = Map.of(
                    "success", true,
                    "data", result
            );

            log.info("Successfully retrieved price range. Returning response: {}", response);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (Exception e) {
            log.error("Failed to get price range", e);
            Map<String, Object> error = Map.of(
                    "success", false,
                    "message", e.getMessage()
            );
            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(error);
        }
    }
} 
