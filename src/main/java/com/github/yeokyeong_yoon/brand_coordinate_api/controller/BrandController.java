package com.github.yeokyeong_yoon.brand_coordinate_api.controller;

import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Brand;
import com.github.yeokyeong_yoon.brand_coordinate_api.dto.BrandRequest;
import com.github.yeokyeong_yoon.brand_coordinate_api.service.AdminService;
import com.github.yeokyeong_yoon.brand_coordinate_api.service.BrandService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/admin/brands")
@CrossOrigin(origins = "*")
public class BrandController {

    private final BrandService brandService;
    private final AdminService adminService;

    public BrandController(BrandService brandService, AdminService adminService) {
        this.brandService = brandService;
        this.adminService = adminService;
    }

    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> test() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Admin brand controller test endpoint is working!");
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(response);
    }

    @PostMapping
    public ResponseEntity<?> registerBrand(@RequestBody BrandRequest request) {
        log.info("Received request to register brand: {}", request.name());
        try {
            Brand brand = adminService.registerBrand(request);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", Map.of(
                "id", brand.getId(),
                "name", brand.getName()
            ));
            log.info("Successfully registered brand: {}", brand.getName());
            return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
        } catch (IllegalArgumentException e) {
            log.error("Failed to register brand: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
        }
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllBrands() {
        log.debug("Received request to get all brands");
        try {
            List<Brand> brands = adminService.getAllBrands();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", brands);
            return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
        } catch (Exception e) {
            log.error("Failed to get all brands: {}", e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(error);
        }
    }

    @PutMapping("/{brandId}")
    public ResponseEntity<?> updateBrand(@PathVariable Long brandId, @RequestBody BrandRequest request) {
        log.info("Received request to update brand {} to name: {}", brandId, request.name());
        try {
            Brand updatedBrand = adminService.updateBrand(brandId, request);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", Map.of(
                "id", updatedBrand.getId(),
                "name", updatedBrand.getName()
            ));
            log.info("Successfully updated brand {} to name: {}", brandId, updatedBrand.getName());
            return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
        } catch (IllegalArgumentException e) {
            log.error("Failed to update brand {}: {}", brandId, e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(error);
        }
    }

    @DeleteMapping("/{brandId}")
    public ResponseEntity<?> deleteBrand(@PathVariable Long brandId) {
        log.info("Received request to delete brand: {}", brandId);
        try {
            adminService.deleteBrand(brandId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Brand deleted successfully");
            log.info("Successfully deleted brand: {}", brandId);
            return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
        } catch (IllegalArgumentException e) {
            log.error("Failed to delete brand {}: {}", brandId, e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(error);
        }
    }
} 