package com.github.yeokyeong_yoon.brand_coordinate_api.controller;

import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Category;
import com.github.yeokyeong_yoon.brand_coordinate_api.dto.CategoryLowestPriceResponse;
import com.github.yeokyeong_yoon.brand_coordinate_api.dto.CategoryPriceResponse;
import com.github.yeokyeong_yoon.brand_coordinate_api.dto.ProductRequest;
import com.github.yeokyeong_yoon.brand_coordinate_api.service.AdminService;
import com.github.yeokyeong_yoon.brand_coordinate_api.service.ProductService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/products")
@CrossOrigin(origins = "*")
public class ProductController {

    private final AdminService adminService;
    private final ProductService productService;

    public ProductController(AdminService adminService, ProductService productService) {
        this.adminService = adminService;
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<?> registerProduct(@RequestBody ProductRequest request) {
        log.info("Backend: Received request for POST /products with data: {}", request);
        try {
            return ResponseEntity.ok(adminService.registerProduct(request));
        } catch (IllegalArgumentException e) {
            log.error("Backend: Error registering product: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @PutMapping("/{productId}")
    public ResponseEntity<?> updateProduct(@PathVariable Long productId, @RequestBody ProductRequest request) {
        log.info("Backend: Received request for PUT /products/{} with data: {}", productId, request);
        try {
            return ResponseEntity.ok(adminService.updateProduct(productId, request));
        } catch (IllegalArgumentException e) {
            log.error("Backend: Error updating product {}: {}", productId, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long productId) {
        log.info("Backend: Received request for DELETE /products/{}", productId);
        try {
            adminService.deleteProduct(productId);
            log.info("Backend: Successfully deleted product {}", productId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            log.error("Backend: Error deleting product {}: {}", productId, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> test() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Test endpoint is working!");
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(response);
    }

    @GetMapping("/lowest-prices")
    public ResponseEntity<Map<String, Object>> getLowestPricesByCategory() {
        log.info("Backend: Received request for GET /products/lowest-prices");
        try {
            CategoryLowestPriceResponse response = productService.findLowestPricesByCategory();
            log.debug("Backend: Generated response: {}", response);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", response);
            
            log.info("Backend: Successfully processed request for lowest prices by category");
            return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(result);
        } catch (Exception e) {
            log.error("Backend: Error processing request for lowest prices by category: {}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(error);
        }
    }

    @GetMapping("/categories/{category}/price-range")
    public ResponseEntity<Map<String, Object>> getPriceRangeByCategory(@PathVariable Category category) {
        log.info("Backend: Received request for GET /products/categories/{}/price-range", category);
        try {
            CategoryPriceResponse response = productService.findPriceRangeByCategory(category);
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