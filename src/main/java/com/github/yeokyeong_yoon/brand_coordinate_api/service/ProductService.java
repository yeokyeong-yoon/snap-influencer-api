package com.github.yeokyeong_yoon.brand_coordinate_api.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Brand;
import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Category;
import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Product;
import com.github.yeokyeong_yoon.brand_coordinate_api.dto.CategoryLowestPriceResponse;
import com.github.yeokyeong_yoon.brand_coordinate_api.dto.CategoryPriceResponse;
import com.github.yeokyeong_yoon.brand_coordinate_api.dto.PriceRangeResponse;
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
@Transactional(readOnly = true)
public class ProductService {
    private final ProductRepository productRepository;
    
    /**
     * 요구사항 1: 카테고리별 최저가격 브랜드와 총액 조회
     */
    public CategoryLowestPriceResponse findLowestPricesByCategory() {
        List<Category> categories = Arrays.asList(Category.values());
        List<CategoryLowestPriceResponse.CategoryPrice> categoryPrices = new ArrayList<>();
        int totalPrice = 0;

        for (Category category : categories) {
            List<Product> products = productRepository.findByCategory(category);
            if (products.isEmpty()) {
                throw new IllegalArgumentException("해당 카테고리의 상품이 없습니다: " + category);
            }

            // 최저가 찾기
            int minPrice = Integer.MAX_VALUE;
            for (Product product : products) {
                minPrice = Math.min(minPrice, product.getPrice());
            }

            // 최저가 브랜드들 찾기
            List<CategoryLowestPriceResponse.CategoryPrice.BrandPrice> brandPrices = new ArrayList<>();
            for (Product product : products) {
                if (product.getPrice() == minPrice) {
                    brandPrices.add(new CategoryLowestPriceResponse.CategoryPrice.BrandPrice(
                        product.getBrand().getName(),
                        product.getPrice()
                    ));
                }
            }

            // 브랜드 이름으로 정렬
            brandPrices.sort((a, b) -> a.brand().compareTo(b.brand()));

            categoryPrices.add(new CategoryLowestPriceResponse.CategoryPrice(
                category.name(),
                brandPrices
            ));
            totalPrice += minPrice;
        }

        // 카테고리 이름으로 정렬
        categoryPrices.sort((a, b) -> a.category().compareTo(b.category()));

        return new CategoryLowestPriceResponse(categoryPrices, totalPrice);
    }

    /**
     * 요구사항 3: 특정 카테고리의 최저가/최고가 브랜드와 가격 조회
     */
    public CategoryPriceResponse findPriceRangeByCategory(Category category) {
        List<Product> products = productRepository.findByCategory(category);
        if (products.isEmpty()) {
            throw new IllegalArgumentException("해당 카테고리의 상품이 없습니다: " + category);
        }

        // 최저가와 최고가 찾기
        int minPrice = Integer.MAX_VALUE;
        int maxPrice = Integer.MIN_VALUE;
        for (Product product : products) {
            minPrice = Math.min(minPrice, product.getPrice());
            maxPrice = Math.max(maxPrice, product.getPrice());
        }

        // 최저가 브랜드들 찾기
        List<CategoryPriceResponse.BrandPrice> lowestPrices = new ArrayList<>();
        List<CategoryPriceResponse.BrandPrice> highestPrices = new ArrayList<>();

        for (Product product : products) {
            if (product.getPrice() == minPrice) {
                lowestPrices.add(new CategoryPriceResponse.BrandPrice(
                    product.getBrand().getName(),
                    product.getPrice()
                ));
            }
            if (product.getPrice() == maxPrice) {
                highestPrices.add(new CategoryPriceResponse.BrandPrice(
                    product.getBrand().getName(),
                    product.getPrice()
                ));
            }
        }

        // 브랜드 이름으로 정렬
        lowestPrices.sort((a, b) -> a.brand().compareTo(b.brand()));
        highestPrices.sort((a, b) -> a.brand().compareTo(b.brand()));

        return new CategoryPriceResponse(
            category.name(),
            lowestPrices,
            highestPrices
        );
    }

    public List<PriceRangeResponse> findPriceRangeByCategory() {
        List<Product> products = productRepository.findAll();
        if (products == null) {
            products = new ArrayList<>();
        }
        Map<Category, List<Product>> productsByCategory = products.stream()
                .collect(Collectors.groupingBy(Product::getCategory));
        List<PriceRangeResponse> priceRanges = new ArrayList<>();
        for (Map.Entry<Category, List<Product>> entry : productsByCategory.entrySet()) {
            Category category = entry.getKey();
            List<Product> categoryProducts = entry.getValue();
            double minPrice = categoryProducts.stream()
                    .mapToDouble(Product::getPrice)
                    .min()
                    .orElse(0.0);
            double maxPrice = categoryProducts.stream()
                    .mapToDouble(Product::getPrice)
                    .max()
                    .orElse(0.0);
            priceRanges.add(new PriceRangeResponse(category.name(), minPrice, maxPrice));
        }
        return priceRanges;
    }
}