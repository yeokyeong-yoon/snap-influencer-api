package com.github.yeokyeong_yoon.brand_coordinate_api.service;

import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Category;
import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Product;
import com.github.yeokyeong_yoon.brand_coordinate_api.dto.CategoryPriceResponse;
import com.github.yeokyeong_yoon.brand_coordinate_api.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Comparator;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {
    private final ProductRepository productRepository;
    
    public List<Category> getAllCategories() {
        return Arrays.asList(Category.values());
    }
    
    public Category getCategoryByName(String name) {
        try {
            return Category.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid category name: " + name);
        }
    }

    public CategoryPriceResponse findPriceRangeByCategory(Category category) {
        var products = productRepository.findByCategory(category);
        if (products.isEmpty()) {
            throw new IllegalArgumentException("해당 카테고리의 상품이 없습니다: " + category);
        }

        // 최저가와 최고가 찾기
        final int minPrice = products.stream()
            .mapToInt(Product::getPrice)
            .min()
            .orElseThrow(() -> new IllegalStateException("최저가를 찾을 수 없습니다."));

        final int maxPrice = products.stream()
            .mapToInt(Product::getPrice)
            .max()
            .orElseThrow(() -> new IllegalStateException("최고가를 찾을 수 없습니다."));

        // 최저가 브랜드들 찾기
        var lowestPrices = products.stream()
            .filter(p -> p.getPrice() == minPrice)
            .map(p -> new CategoryPriceResponse.BrandPrice(p.getBrand().getName(), p.getPrice()))
            .sorted(Comparator.comparing(CategoryPriceResponse.BrandPrice::brand))
            .collect(Collectors.toList());

        // 최고가 브랜드들 찾기
        var highestPrices = products.stream()
            .filter(p -> p.getPrice() == maxPrice)
            .map(p -> new CategoryPriceResponse.BrandPrice(p.getBrand().getName(), p.getPrice()))
            .sorted(Comparator.comparing(CategoryPriceResponse.BrandPrice::brand))
            .collect(Collectors.toList());

        return new CategoryPriceResponse(category.name(), lowestPrices, highestPrices);
    }
} 