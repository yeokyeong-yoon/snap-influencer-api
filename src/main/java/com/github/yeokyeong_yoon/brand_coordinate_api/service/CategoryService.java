package com.github.yeokyeong_yoon.brand_coordinate_api.service;

import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Category;
import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Product;
import com.github.yeokyeong_yoon.brand_coordinate_api.dto.PriceComparisonResponse;
import com.github.yeokyeong_yoon.brand_coordinate_api.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 카테고리 관련 비즈니스 로직을 처리하는 서비스입니다.
 * 
 * Spring의 기본적인 기능들을 사용합니다:
 * 1. @Service: 이 클래스가 서비스 계층임을 Spring에게 알려줍니다.
 * 2. @RequiredArgsConstructor: 생성자 주입을 자동으로 해줍니다.
 * 3. @Transactional: 데이터베이스 작업의 일관성을 보장합니다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {
    private final ProductRepository productRepository;

    public List<PriceComparisonResponse.ByCategory> getPriceRangeByCategory() {
        return List.of(Category.values())
                .stream()
                .map(category -> {
                    List<Product> products = productRepository.findPriceRangeProductsByCategory(category);
                    List<PriceComparisonResponse.ProductPrice> cheapestProducts = products.stream()
                            .filter(p -> p.getPrice() == products.stream().mapToInt(Product::getPrice).min().orElse(0))
                            .map(p -> new PriceComparisonResponse.ProductPrice(p.getBrand().getName(), p.getPrice()))
                            .collect(Collectors.toList());
                    
                    List<PriceComparisonResponse.ProductPrice> expensiveProducts = products.stream()
                            .filter(p -> p.getPrice() == products.stream().mapToInt(Product::getPrice).max().orElse(0))
                            .map(p -> new PriceComparisonResponse.ProductPrice(p.getBrand().getName(), p.getPrice()))
                            .collect(Collectors.toList());

                    return new PriceComparisonResponse.ByCategory(
                            category.name(),
                            cheapestProducts,
                            expensiveProducts
                    );
                })
                .collect(Collectors.toList());
    }
} 