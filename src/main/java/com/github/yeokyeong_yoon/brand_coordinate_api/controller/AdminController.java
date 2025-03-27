package com.github.yeokyeong_yoon.brand_coordinate_api.controller;

import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Brand;
import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Product;
import com.github.yeokyeong_yoon.brand_coordinate_api.dto.BrandRequest;
import com.github.yeokyeong_yoon.brand_coordinate_api.dto.ProductRequest;
import com.github.yeokyeong_yoon.brand_coordinate_api.service.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/brands")
    public ResponseEntity<Map<String, Object>> registerBrand(@RequestBody BrandRequest request) {
        log.info("Received request to register brand. Request body: {}", request);
        try {
            log.debug("Calling adminService.registerBrand() with request: {}", request);
            var brand = adminService.registerBrand(request);
            log.debug("Received response from service: {}", brand);
            
            Map<String, Object> response = Map.of(
                "success", true,
                "data", brand
            );
            
            log.info("Successfully registered brand. Returning response: {}", response);
            return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
        } catch (Exception e) {
            log.error("Failed to register brand", e);
            Map<String, Object> error = Map.of(
                "success", false,
                "message", e.getMessage()
            );
            return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(error);
        }
    }

    @PutMapping("/brands/{brandId}")
    public ResponseEntity<Map<String, Object>> updateBrand(@PathVariable Long brandId, @RequestBody BrandRequest request) {
        try {
            Brand brand = adminService.updateBrand(brandId, request);
            Map<String, Object> response = Map.of(
                "success", true,
                "data", Map.of(
                    "id", brand.getId(),
                    "name", brand.getName()
                )
            );
            
            return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
        } catch (IllegalArgumentException e) {
            log.error("Failed to update brand {}: {}", brandId, e.getMessage());
            Map<String, Object> error = Map.of(
                "success", false,
                "message", e.getMessage()
            );
            return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(error);
        }
    }

    @DeleteMapping("/brands/{brandId}")
    public ResponseEntity<Map<String, Object>> deleteBrand(@PathVariable Long brandId) {
        try {
            adminService.deleteBrand(brandId);
            Map<String, Object> response = Map.of(
                "success", true,
                "message", "브랜드가 성공적으로 삭제되었습니다."
            );
            
            return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
        } catch (IllegalArgumentException e) {
            log.error("Failed to delete brand {}: {}", brandId, e.getMessage());
            Map<String, Object> error = Map.of(
                "success", false,
                "message", e.getMessage()
            );
            return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(error);
        }
    }

    @GetMapping("/products")
    public ResponseEntity<Map<String, Object>> getAllProducts() {
        log.info("Received request to get all products");
        try {
            log.debug("Calling adminService.getAllProducts()");
            var products = adminService.getAllProducts();
            log.debug("Received {} products from service", products.size());
            
            Map<String, Object> response = Map.of(
                "success", true,
                "data", products
            );
            
            log.info("Successfully retrieved products. Returning response: {}", response);
            return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
        } catch (Exception e) {
            log.error("Failed to get products", e);
            Map<String, Object> error = Map.of(
                "success", false,
                "message", e.getMessage()
            );
            return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(error);
        }
    }

    @PostMapping("/products")
    public ResponseEntity<Map<String, Object>> registerProduct(@RequestBody ProductRequest request) {
        log.info("Received request to register product. Request body: {}", request);
        try {
            log.debug("Validating product request...");
            if (request.brand() == null || request.brand().trim().isEmpty()) {
                throw new IllegalArgumentException("브랜드 이름은 필수입니다.");
            }
            if (request.category() == null) {
                throw new IllegalArgumentException("카테고리는 필수입니다.");
            }
            if (request.price() <= 0) {
                throw new IllegalArgumentException("가격은 0보다 커야 합니다.");
            }
            
            log.debug("Calling adminService.registerProduct() with request: {}", request);
            var product = adminService.registerProduct(request);
            log.debug("Received response from service: {}", product);
            
            Map<String, Object> response = Map.of(
                "success", true,
                "data", product
            );
            
            log.info("Successfully registered product. Returning response: {}", response);
            return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
        } catch (Exception e) {
            log.error("Failed to register product. Request: {}, Error: {}", request, e.getMessage(), e);
            Map<String, Object> error = Map.of(
                "success", false,
                "message", e.getMessage()
            );
            return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(error);
        }
    }

    @DeleteMapping("/products/{productId}")
    public ResponseEntity<Map<String, Object>> deleteProduct(@PathVariable Long productId) {
        log.info("Received request to delete product with ID: {}", productId);
        try {
            log.debug("Calling adminService.deleteProduct() with ID: {}", productId);
            adminService.deleteProduct(productId);
            
            Map<String, Object> response = Map.of(
                "success", true,
                "message", "Product deleted successfully"
            );
            
            log.info("Successfully deleted product. Returning response: {}", response);
            return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
        } catch (Exception e) {
            log.error("Failed to delete product with ID: {}", productId, e);
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