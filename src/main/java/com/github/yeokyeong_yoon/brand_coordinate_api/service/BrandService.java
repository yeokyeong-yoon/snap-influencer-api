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
import lombok.extern.slf4j.Slf4j;

/**
 * 브랜드 관련 비즈니스 로직을 처리하는 서비스입니다.
 * 
 * Spring의 기본적인 기능들을 사용합니다:
 * 1. @Service: 이 클래스가 서비스 계층임을 Spring에게 알려줍니다.
 * 2. @RequiredArgsConstructor: 생성자 주입을 자동으로 해줍니다.
 * 3. @Transactional: 데이터베이스 작업의 일관성을 보장합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BrandService {
    private final BrandRepository brandRepository;
    private final ProductRepository productRepository;

    // Frontend 지원: 브랜드 목록 조회
    public List<Brand> getAllBrands() {
        log.debug("Fetching all brands");
        return brandRepository.findAll();
    }

    /**
     * 구현 2) 단일 브랜드로 모든 카테고리 상품을 구매할 때 최저가격에 판매하는 브랜드와 카테고리의 상품가격, 총액을 조회
     */
    public CheapestBrandResponse findCheapestBrandTotal(List<Category> selectedCategories) {
        log.info("Starting findCheapestBrandTotal with categories: {}", selectedCategories);
        
        if (selectedCategories == null || selectedCategories.isEmpty()) {
            log.error("No categories selected");
            throw new IllegalArgumentException("카테고리를 선택해주세요");
        }

        List<Product> allProducts = productRepository.findAll();
        log.debug("Found {} total products", allProducts.size());
        
        if (allProducts.isEmpty()) {
            log.error("No products found in the database");
            throw new IllegalArgumentException("상품이 없습니다");
        }

        Map<Brand, Map<Category, Integer>> brandPrices = new HashMap<>();
        Map<Brand, Integer> brandTotals = new HashMap<>();

        // Filter products by selected categories
        List<Product> selectedProducts = allProducts.stream()
            .filter(product -> selectedCategories.contains(product.getCategory()))
            .collect(Collectors.toList());

        if (selectedProducts.isEmpty()) {
            log.error("No products found for selected categories");
            throw new IllegalArgumentException("선택한 카테고리의 상품이 없습니다");
        }

        for (Product product : selectedProducts) {
            Brand brand = product.getBrand();
            Category category = product.getCategory();
            int price = product.getPrice();

            Map<Category, Integer> categoryPrices = brandPrices.computeIfAbsent(brand, k -> new HashMap<>());
            categoryPrices.merge(category, price, Math::min);
            brandTotals.merge(brand, price, Integer::sum);
        }
        log.debug("Processed {} brands with their prices", brandPrices.size());

        // Find brands that have all selected categories
        List<Brand> brandsWithAllCategories = brandPrices.entrySet().stream()
            .filter(entry -> entry.getValue().keySet().containsAll(selectedCategories))
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());

        if (brandsWithAllCategories.isEmpty()) {
            log.error("No brands found with all selected categories");
            return new CheapestBrandResponse(new ArrayList<>());
        }

        // Find minimum total among brands with all categories
        int minTotal = brandsWithAllCategories.stream()
            .mapToInt(brand -> selectedCategories.stream()
                .mapToInt(category -> brandPrices.get(brand).get(category))
                .sum())
            .min()
            .orElse(Integer.MAX_VALUE);
        log.debug("Found minimum total price: {}", minTotal);

        List<CheapestBrandResponse.BrandTotal> result = new ArrayList<>();
        for (Brand brand : brandsWithAllCategories) {
            int total = selectedCategories.stream()
                .mapToInt(category -> brandPrices.get(brand).get(category))
                .sum();

            if (total == minTotal) {
                log.debug("Processing brand with minimum total: {}", brand.getName());
                List<CheapestBrandResponse.CategoryPrice> categoryPrices = new ArrayList<>();
                
                for (Category category : selectedCategories) {
                    categoryPrices.add(new CheapestBrandResponse.CategoryPrice(
                        category.name(),
                        brandPrices.get(brand).get(category)
                    ));
                }
                
                categoryPrices.sort(Comparator.comparing(CheapestBrandResponse.CategoryPrice::category));
                log.debug("Found {} categories for brand {}", categoryPrices.size(), brand.getName());

                result.add(new CheapestBrandResponse.BrandTotal(
                    brand.getName(),
                    minTotal,
                    categoryPrices
                ));
            }
        }

        result.sort(Comparator.comparing(CheapestBrandResponse.BrandTotal::brand));
        log.info("Completed findCheapestBrandTotal with {} brands having minimum total price", result.size());
        
        return new CheapestBrandResponse(result);
    }
}