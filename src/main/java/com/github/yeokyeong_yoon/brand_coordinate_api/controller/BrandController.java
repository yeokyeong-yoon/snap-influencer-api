package com.github.yeokyeong_yoon.brand_coordinate_api.controller;

import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Brand;
import com.github.yeokyeong_yoon.brand_coordinate_api.dto.BrandRequest;
import com.github.yeokyeong_yoon.brand_coordinate_api.dto.CheapestBrandResponse;
import com.github.yeokyeong_yoon.brand_coordinate_api.service.AdminService;
import com.github.yeokyeong_yoon.brand_coordinate_api.service.BrandService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/brands")
public class BrandController {

    private final BrandService brandService;
    private final AdminService adminService;

    public BrandController(BrandService brandService, AdminService adminService) {
        this.brandService = brandService;
        this.adminService = adminService;
    }

    // Frontend 지원: 브랜드 목록 조회
    @GetMapping
    public List<Brand> getAllBrands() {
        return brandService.getAllBrands();
    }

    // 2. 고객은 단일 브랜드로 전체 카테고리 상품을 구매할 경우 최저가격인 브랜드와 총액이 얼마인지 확인할 수 있어야 합니다.
    @GetMapping("/cheapest")
    public CheapestBrandResponse getCheapestTotalBrand() {
        return brandService.findCheapestBrandTotal();
    }

    // 4. 운영자는 새로운 브랜드를 등록할 수 있어야 합니다.
    @PostMapping
    public ResponseEntity<?> registerBrand(@RequestBody BrandRequest request) {
        try {
            return ResponseEntity.ok(adminService.registerBrand(request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    // Frontend 지원: 브랜드 수정 기능
    @PutMapping("/{brandId}")
    public ResponseEntity<?> updateBrand(@PathVariable Long brandId, @RequestBody BrandRequest request) {
        try {
            return ResponseEntity.ok(adminService.updateBrand(brandId, request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    // Frontend 지원: 브랜드 삭제 기능
    @DeleteMapping("/{brandId}")
    public ResponseEntity<?> deleteBrand(@PathVariable Long brandId) {
        try {
            adminService.deleteBrand(brandId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
} 