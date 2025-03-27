package com.github.yeokyeong_yoon.brand_coordinate_api.controller;

import com.github.yeokyeong_yoon.brand_coordinate_api.dto.BrandRequest;
import com.github.yeokyeong_yoon.brand_coordinate_api.dto.PriceComparisonResponse;
import com.github.yeokyeong_yoon.brand_coordinate_api.service.AdminService;
import com.github.yeokyeong_yoon.brand_coordinate_api.service.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/brands")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;
    private final AdminService adminService;

    // 2. 고객은 단일 브랜드로 전체 카테고리 상품을 구매할 경우 최저가격인 브랜드와 총액이 얼마인지 확인할 수 있어야 합니다.
    @GetMapping("/cheapest")
    public List<PriceComparisonResponse.ByBrand> getCheapestTotalBrand() {
        return brandService.getCheapestTotalBrand();
    }

    @PostMapping
    public ResponseEntity<?> registerBrand(@RequestBody BrandRequest request) {
        try {
            return ResponseEntity.ok(adminService.registerBrand(request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBrand(@PathVariable Long id, @RequestBody BrandRequest request) {
        try {
            return ResponseEntity.ok(adminService.updateBrand(id, request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBrand(@PathVariable Long id) {
        try {
            adminService.deleteBrand(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
} 