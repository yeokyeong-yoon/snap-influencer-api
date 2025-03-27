package com.github.yeokyeong_yoon.brand_coordinate_api.service;

import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Category;
import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Product;
import com.github.yeokyeong_yoon.brand_coordinate_api.dto.CategoryLowestPriceResponse;
import com.github.yeokyeong_yoon.brand_coordinate_api.dto.CategoryPriceResponse;
import com.github.yeokyeong_yoon.brand_coordinate_api.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 카테고리 관련 비즈니스 로직을 처리하는 서비스입니다.
 * 
 * Spring의 기본적인 기능들을 사용합니다:
 * 1. @Service: 이 클래스가 서비스 계층임을 Spring에게 알려줍니다.
 * 2. @RequiredArgsConstructor: 생성자 주입을 자동으로 해줍니다.
 * 3. @Transactional: 데이터베이스 작업의 일관성을 보장합니다.
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class CategoryService {

    private final ProductRepository productRepository;

    public CategoryService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * 구현 1) 카테고리별 최저가격 브랜드와 상품 가격, 총액을 조회
     */
    public CategoryLowestPriceResponse findLowestPricesByCategory() {
        log.info("Starting findLowestPricesByCategory");
        List<Category> categories = Arrays.asList(Category.values());
        List<CategoryLowestPriceResponse.CategoryPrice> categoryPrices = new ArrayList<>();
        int totalPrice = 0;

        for (Category category : categories) {
            log.debug("Processing category: {}", category);
            List<Product> products = productRepository.findByCategory(category);
            log.debug("Found {} products for category {}", products.size(), category);
            
            if (products.isEmpty()) {
                log.error("No products found for category: {}", category);
                throw new IllegalArgumentException("해당 카테고리의 상품이 없습니다: " + category);
            }

            // 최저가 찾기
            int minPrice = Integer.MAX_VALUE;
            for (Product product : products) {
                minPrice = Math.min(minPrice, product.getPrice());
            }
            log.debug("Minimum price for category {}: {}", category, minPrice);

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
            log.debug("Found {} brands with minimum price for category {}", brandPrices.size(), category);

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

        CategoryLowestPriceResponse response = new CategoryLowestPriceResponse(categoryPrices, totalPrice);
        log.info("Completed findLowestPricesByCategory with {} categories and total price {}", 
                categoryPrices.size(), totalPrice);
        return response;
    }

    /**
     * 구현 3) 카테고리 이름으로 최저, 최고 가격 브랜드와 상품 가격을 조회
     */
    public CategoryPriceResponse findPriceRangeByCategory(Category category) {
        log.info("Starting findPriceRangeByCategory for category: {}", category);
        List<Product> products = productRepository.findByCategory(category);
        log.debug("Found {} products for category {}", products.size(), category);
        
        if (products.isEmpty()) {
            log.error("No products found for category: {}", category);
            throw new IllegalArgumentException("해당 카테고리의 상품이 없습니다: " + category);
        }

        // 최저가와 최고가 찾기
        int minPrice = Integer.MAX_VALUE;
        int maxPrice = Integer.MIN_VALUE;
        for (Product product : products) {
            minPrice = Math.min(minPrice, product.getPrice());
            maxPrice = Math.max(maxPrice, product.getPrice());
        }
        log.debug("Price range for category {}: min={}, max={}", category, minPrice, maxPrice);

        // 최저가와 최고가 브랜드들 찾기
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
        log.debug("Found {} lowest price brands and {} highest price brands for category {}", 
                lowestPrices.size(), highestPrices.size(), category);

        // 브랜드 이름으로 정렬
        lowestPrices.sort((a, b) -> a.brand().compareTo(b.brand()));
        highestPrices.sort((a, b) -> a.brand().compareTo(b.brand()));

        CategoryPriceResponse response = new CategoryPriceResponse(
            category.name(),
            lowestPrices,
            highestPrices
        );
        log.info("Completed findPriceRangeByCategory for category: {}", category);
        return response;
    }
} 