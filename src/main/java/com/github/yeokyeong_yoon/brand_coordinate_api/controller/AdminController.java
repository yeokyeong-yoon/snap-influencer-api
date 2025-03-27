package com.github.yeokyeong_yoon.brand_coordinate_api.controller;

import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Product;
import com.github.yeokyeong_yoon.brand_coordinate_api.dto.ProductRequest;
import com.github.yeokyeong_yoon.brand_coordinate_api.service.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> test() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Admin test endpoint is working!");
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(response);
    }

    @GetMapping("/products")
    public ResponseEntity<Map<String, Object>> getAllProducts() {
        log.debug("Received request to get all products");
        try {
            List<Product> products = adminService.getAllProducts();
            List<Map<String, Object>> productList = new ArrayList<>();
            
            for (Product product : products) {
                Map<String, Object> productMap = new HashMap<>();
                productMap.put("id", product.getId());
                productMap.put("brand", product.getBrand().getName());
                productMap.put("category", product.getCategory().name());
                productMap.put("price", product.getPrice());
                productList.add(productMap);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", productList);
            
            log.debug("Successfully retrieved {} products", productList.size());
            return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
        } catch (Exception e) {
            log.error("Failed to get all products: {}", e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(error);
        }
    }

    @PostMapping("/products")
    public ResponseEntity<?> registerProduct(@RequestBody ProductRequest request) {
        log.info("Received request to register product for brand: {}, category: {}, price: {}", 
                request.brand(), request.category(), request.price());
        try {
            Product product = adminService.registerProduct(request);
            Map<String, Object> data = new HashMap<>();
            data.put("id", product.getId());
            data.put("brand", product.getBrand().getName());
            data.put("category", product.getCategory().name());
            data.put("price", product.getPrice());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", data);
            
            log.info("Successfully registered product with id: {} for brand: {}", 
                    product.getId(), product.getBrand().getName());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("Failed to register product: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/products/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long productId) {
        log.info("Received request to delete product: {}", productId);
        try {
            adminService.deleteProduct(productId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Product deleted successfully");
            log.info("Successfully deleted product: {}", productId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("Failed to delete product {}: {}", productId, e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
} 