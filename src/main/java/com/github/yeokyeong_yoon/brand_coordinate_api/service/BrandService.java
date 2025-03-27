package com.github.yeokyeong_yoon.brand_coordinate_api.service;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Brand;
import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Category;
import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Product;
import com.github.yeokyeong_yoon.brand_coordinate_api.dto.CheapestBrandResponse;
import com.github.yeokyeong_yoon.brand_coordinate_api.repository.BrandRepository;
import com.github.yeokyeong_yoon.brand_coordinate_api.repository.ProductRepository;
import lombok.RequiredArgsConstructor;

/**
 * 브랜드 관련 비즈니스 로직을 처리하는 서비스입니다.
 * 
 * Spring의 기본적인 기능들을 사용합니다:
 * 1. @Service: 이 클래스가 서비스 계층임을 Spring에게 알려줍니다.
 * 2. @RequiredArgsConstructor: 생성자 주입을 자동으로 해줍니다.
 * 3. @Transactional: 데이터베이스 작업의 일관성을 보장합니다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BrandService {
    private final BrandRepository brandRepository;
    private final ProductRepository productRepository;

    // Frontend 지원: 브랜드 목록 조회
    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }

    /**
     * 구현 2) 단일 브랜드로 모든 카테고리 상품을 구매할 때 최저가격에 판매하는 브랜드와 카테고리의 상품가격, 총액을 조회
     */
    public CheapestBrandResponse findCheapestBrandTotal() {
        List<Product> allProducts = productRepository.findAll();
        if (allProducts.isEmpty()) {
            throw new IllegalArgumentException("상품이 없습니다");
        }

        // 브랜드별 카테고리별 가격 맵 생성
        Map<Brand, Map<Category, Integer>> brandCategoryPrices = new HashMap<>();
        
        // 브랜드별 총액 계산
        Map<Brand, Integer> brandTotals = new HashMap<>();

        // 각 브랜드의 카테고리별 가격과 총액 계산
        for (Product product : allProducts) {
            Brand brand = product.getBrand();
            Category category = product.getCategory();
            int price = product.getPrice();

            // 브랜드의 카테고리별 가격 맵 업데이트
            brandCategoryPrices.computeIfAbsent(brand, k -> new HashMap<>())
                    .merge(category, price, Math::min);  // 각 카테고리의 최저가만 사용

            // 브랜드별 총액 업데이트
            brandTotals.merge(brand, price, Integer::sum);
        }

        // 최저 총액 찾기
        int minTotal = brandTotals.values().stream()
                .mapToInt(Integer::intValue)
                .min()
                .orElseThrow();

        // 최저 총액을 가진 브랜드들의 정보 생성
        List<CheapestBrandResponse.BrandTotal> brandTotalList = brandTotals.entrySet().stream()
                .filter(entry -> entry.getValue() == minTotal)
                .map(entry -> {
                    Brand brand = entry.getKey();
                    List<CheapestBrandResponse.BrandTotal.CategoryPrice> categoryPrices = 
                            brandCategoryPrices.get(brand).entrySet().stream()
                                    .map(categoryEntry -> new CheapestBrandResponse.BrandTotal.CategoryPrice(
                                            categoryEntry.getKey().name(),
                                            categoryEntry.getValue()
                                    ))
                                    .sorted(Comparator.comparing(CheapestBrandResponse.BrandTotal.CategoryPrice::category))
                                    .toList();

                    return new CheapestBrandResponse.BrandTotal(
                            brand.getName(),
                            minTotal,
                            categoryPrices
                    );
                })
                .sorted(Comparator.comparing(CheapestBrandResponse.BrandTotal::brand))
                .toList();

        return new CheapestBrandResponse(brandTotalList);
    }
}