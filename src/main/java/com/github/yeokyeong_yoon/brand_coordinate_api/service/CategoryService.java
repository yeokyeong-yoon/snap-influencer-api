package com.github.yeokyeong_yoon.brand_coordinate_api.service;

import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Category;
import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Product;
import com.github.yeokyeong_yoon.brand_coordinate_api.dto.CategoryPriceComparisonResponse;
import com.github.yeokyeong_yoon.brand_coordinate_api.dto.ProductPriceDto;
import com.github.yeokyeong_yoon.brand_coordinate_api.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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
public class CategoryService {
    private final ProductRepository productRepository;

    /**
     * 특정 카테고리에서 가장 싼 상품과 가장 비싼 상품을 찾아 반환합니다.
     */
    @Transactional(readOnly = true)
    public CategoryPriceComparisonResponse comparePricesInCategory(Category category) {
        List<Product> products = productRepository.findPriceRangeProductsByCategory(category.name());
        
        if (products.isEmpty()) {
            throw new IllegalArgumentException("No products found for category: " + category);
        }

        PriceRange priceRange = findPriceRange(products);
        List<ProductPriceDto> cheapestProducts = findProductsByPrice(products, priceRange.minPrice());
        List<ProductPriceDto> mostExpensiveProducts = findProductsByPrice(products, priceRange.maxPrice());

        return new CategoryPriceComparisonResponse(
                category.name(),
                cheapestProducts,
                mostExpensiveProducts
        );
    }

    // 최소 가격과 최대 가격을 저장하는 클래스입니다. 이 클래스는 CategoryService의 인스턴스와 무관하게 사용될 수 있으므로 static으로 선언되었습니다.
    private static class PriceRange {
        private final int minPrice;
        private final int maxPrice;

        public PriceRange(int minPrice, int maxPrice) {
            this.minPrice = minPrice;
            this.maxPrice = maxPrice;
        }

        public int minPrice() {
            return minPrice;
        }

        public int maxPrice() {
            return maxPrice;
        }
    }

    // 상품 목록에서 최소 가격과 최대 가격을 찾습니다.
    private PriceRange findPriceRange(List<Product> products) {
        int minPrice = Integer.MAX_VALUE;
        int maxPrice = Integer.MIN_VALUE;

        for (Product product : products) {
            int price = product.getPrice();
            if (price < minPrice) {
                minPrice = price;
            }
            if (price > maxPrice) {
                maxPrice = price;
            }
        }

        return new PriceRange(minPrice, maxPrice);
    }

    // 특정 가격을 가진 상품들을 찾습니다.
    private List<ProductPriceDto> findProductsByPrice(List<Product> products, int targetPrice) {
        List<ProductPriceDto> result = new ArrayList<>();
        for (Product product : products) {
            if (product.getPrice() == targetPrice) {
                result.add(toProductPriceDto(product));
            }
        }
        return result;
    }

    // 상품 정보를 DTO로 변환합니다.
    private ProductPriceDto toProductPriceDto(Product product) {
        return new ProductPriceDto(
                product.getBrand().getName(),
                product.getPrice()
        );
    }
} 