package com.github.yeokyeong_yoon.brand_coordinate_api.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

        // 브랜드별 카테고리별 최저가격 계산
        Map<Brand, Map<Category, Integer>> brandPrices = new HashMap<>();
        Map<Brand, Integer> brandTotals = new HashMap<>();

        // 각 브랜드의 카테고리별 최저가격과 총액 계산
        for (Product product : allProducts) {
            Brand brand = product.getBrand();
            Category category = product.getCategory();
            int price = product.getPrice();

            // 카테고리별 최저가 업데이트
            Map<Category, Integer> categoryPrices = brandPrices.computeIfAbsent(brand, k -> new HashMap<>());
            categoryPrices.merge(category, price, Math::min);

            // 총액 업데이트
            brandTotals.merge(brand, price, Integer::sum);
        }

        // 최저 총액 찾기
        int minTotal = Integer.MAX_VALUE;
        for (int total : brandTotals.values()) {
            if (total < minTotal) {
                minTotal = total;
            }
        }

        // 최저 총액을 가진 브랜드들의 정보 생성
        List<CheapestBrandResponse.BrandTotal> result = new ArrayList<>();
        for (Map.Entry<Brand, Integer> entry : brandTotals.entrySet()) {
            if (entry.getValue() == minTotal) {
                Brand brand = entry.getKey();
                List<CheapestBrandResponse.BrandTotal.CategoryPrice> categoryPrices = new ArrayList<>();
                
                for (Map.Entry<Category, Integer> priceEntry : brandPrices.get(brand).entrySet()) {
                    categoryPrices.add(new CheapestBrandResponse.BrandTotal.CategoryPrice(
                        priceEntry.getKey().name(),
                        priceEntry.getValue()
                    ));
                }
                
                // 카테고리 이름으로 정렬
                categoryPrices.sort((a, b) -> a.category().compareTo(b.category()));

                result.add(new CheapestBrandResponse.BrandTotal(
                    brand.getName(),
                    minTotal,
                    categoryPrices
                ));
            }
        }

        // 브랜드 이름으로 정렬
        result.sort((a, b) -> a.brand().compareTo(b.brand()));

        return new CheapestBrandResponse(result);
    }
}