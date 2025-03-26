package com.github.yeokyeong_yoon.brand_coordinate_api.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Category;
import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Product;
import com.github.yeokyeong_yoon.brand_coordinate_api.dto.CheapestByCategoryResponse;
import com.github.yeokyeong_yoon.brand_coordinate_api.dto.CheapestProductDto;
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
     * 각 카테고리별로 가장 싼 상품을 찾고, 그 상품들의 총 가격을 계산합니다.
     */
    @Transactional(readOnly = true)
    public CheapestByCategoryResponse getCheapestByCategory() {
        List<CheapestProductDto> results = new ArrayList<>();
        int total = 0;

        for (Category category : Category.values()) {
            Product cheapestProduct = findCheapestProductInCategory(category);
            results.add(new CheapestProductDto(
                category.name(), 
                cheapestProduct.getBrand().getName(), 
                cheapestProduct.getPrice()
            ));
            total += cheapestProduct.getPrice();
        }

        return new CheapestByCategoryResponse(results, total);
    }

    // 특정 카테고리에서 가장 싼 상품을 찾습니다.
    private Product findCheapestProductInCategory(Category category) {
        List<Product> products = productRepository.findByCategory(category);
        if (products.isEmpty()) {
            throw new IllegalArgumentException("No product found for category: " + category);
        }

        Product cheapest = products.get(0);
        for (Product product : products) {
            if (product.getPrice() < cheapest.getPrice()) {
                cheapest = product;
            }
        }
        return cheapest;
    }
}