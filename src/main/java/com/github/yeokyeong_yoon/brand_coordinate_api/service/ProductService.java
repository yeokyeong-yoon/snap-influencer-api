package com.github.yeokyeong_yoon.brand_coordinate_api.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Brand;
import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Category;
import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Product;
import com.github.yeokyeong_yoon.brand_coordinate_api.dto.CategoryLowestPriceResponse;
import com.github.yeokyeong_yoon.brand_coordinate_api.dto.CategoryPriceResponse;
import com.github.yeokyeong_yoon.brand_coordinate_api.dto.CheapestBrandResponse;
import com.github.yeokyeong_yoon.brand_coordinate_api.repository.ProductRepository;
import lombok.RequiredArgsConstructor;

/**
 * 상품 관련 비즈니스 로직을 처리하는 서비스입니다.
 * 
 * Spring의 기본적인 기능들을 사용합니다:
 * 1. @Service: 이 클래스가 서비스 계층임을 Spring에게 알려줍니다.
 * 2. @RequiredArgsConstructor: 생성자 주입을 자동으로 해줍니다.
 * 3. @Transactional: 데이터베이스 작업의 일관성을 보장합니다.
 */
@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    
    /**
     * 요구사항 1: 카테고리별 최저가격 브랜드와 총액 조회
     */
    @Transactional(readOnly = true)
    public CategoryLowestPriceResponse findLowestPricesByCategory() {
        List<CategoryLowestPriceResponse.CategoryPrice> categories = new ArrayList<>();
        int totalPrice = 0;

        for (Category category : Category.values()) {
            List<Product> products = productRepository.findByCategory(category);
            if (products.isEmpty()) {
                throw new IllegalArgumentException("해당 카테고리의 상품이 없습니다: " + category);
            }

            int minPrice = products.stream()
                .mapToInt(Product::getPrice)
                .min()
                .getAsInt();

            List<CategoryLowestPriceResponse.CategoryPrice.BrandPrice> brandPrices = products.stream()
                .filter(p -> p.getPrice() == minPrice)
                .map(p -> new CategoryLowestPriceResponse.CategoryPrice.BrandPrice(
                    p.getBrand().getName(),
                    p.getPrice()
                ))
                .collect(Collectors.toList());

            categories.add(new CategoryLowestPriceResponse.CategoryPrice(
                category.name(),
                brandPrices
            ));
            
            totalPrice += minPrice;
        }

        return new CategoryLowestPriceResponse(categories, totalPrice);
    }

    /**
     * 요구사항 2: 단일 브랜드로 전체 카테고리 구매시 최저가격 브랜드와 총액 조회
     */
    @Transactional(readOnly = true)
    public CheapestBrandResponse findCheapestBrandTotal() {
        Map<Brand, List<Product>> productsByBrand = productRepository.findAll().stream()
            .collect(Collectors.groupingBy(Product::getBrand));

        List<CheapestBrandResponse.BrandTotal> brandTotals = new ArrayList<>();
        int lowestTotal = Integer.MAX_VALUE;

        // 첫 번째 패스: 최저 총액 찾기
        for (Map.Entry<Brand, List<Product>> entry : productsByBrand.entrySet()) {
            List<Product> products = entry.getValue();
            
            // 모든 카테고리의 상품이 있는지 확인
            if (products.size() != Category.values().length) {
                continue;
            }

            int total = products.stream()
                .mapToInt(Product::getPrice)
                .sum();

            if (total < lowestTotal) {
                lowestTotal = total;
            }
        }

        // 두 번째 패스: 최저 총액과 일치하는 브랜드들 찾기
        for (Map.Entry<Brand, List<Product>> entry : productsByBrand.entrySet()) {
            Brand brand = entry.getKey();
            List<Product> products = entry.getValue();
            
            if (products.size() != Category.values().length) {
                continue;
            }

            int total = products.stream()
                .mapToInt(Product::getPrice)
                .sum();

            if (total == lowestTotal) {
                List<CheapestBrandResponse.BrandTotal.CategoryPrice> categoryPrices = products.stream()
                    .map(p -> new CheapestBrandResponse.BrandTotal.CategoryPrice(
                        p.getCategory().name(),
                        p.getPrice()
                    ))
                    .sorted(Comparator.comparing(CheapestBrandResponse.BrandTotal.CategoryPrice::category))
                    .collect(Collectors.toList());

                brandTotals.add(new CheapestBrandResponse.BrandTotal(
                    brand.getName(),
                    total,
                    categoryPrices
                ));
            }
        }

        if (brandTotals.isEmpty()) {
            throw new IllegalStateException("모든 카테고리의 상품을 보유한 브랜드가 없습니다.");
        }

        return new CheapestBrandResponse(brandTotals);
    }

    /**
     * 요구사항 3: 특정 카테고리의 최저가/최고가 브랜드와 가격 조회
     */
    @Transactional(readOnly = true)
    public CategoryPriceResponse findPriceRangeByCategory(Category category) {
        List<Product> products = productRepository.findByCategory(category);
        if (products.isEmpty()) {
            throw new IllegalArgumentException("해당 카테고리의 상품이 없습니다: " + category);
        }

        int minPrice = products.stream()
            .mapToInt(Product::getPrice)
            .min()
            .getAsInt();

        int maxPrice = products.stream()
            .mapToInt(Product::getPrice)
            .max()
            .getAsInt();

        List<CategoryPriceResponse.BrandPrice> lowestPrices = products.stream()
            .filter(p -> p.getPrice() == minPrice)
            .map(p -> new CategoryPriceResponse.BrandPrice(
                p.getBrand().getName(),
                p.getPrice()
            ))
            .collect(Collectors.toList());

        List<CategoryPriceResponse.BrandPrice> highestPrices = products.stream()
            .filter(p -> p.getPrice() == maxPrice)
            .map(p -> new CategoryPriceResponse.BrandPrice(
                p.getBrand().getName(),
                p.getPrice()
            ))
            .collect(Collectors.toList());

        return new CategoryPriceResponse(
            category.name(),
            lowestPrices,
            highestPrices
        );
    }
}