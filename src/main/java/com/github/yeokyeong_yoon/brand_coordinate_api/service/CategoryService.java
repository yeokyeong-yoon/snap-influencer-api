package com.github.yeokyeong_yoon.brand_coordinate_api.service;

import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Category;
import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Product;
import com.github.yeokyeong_yoon.brand_coordinate_api.dto.CategoryLowestPriceResponse;
import com.github.yeokyeong_yoon.brand_coordinate_api.dto.CategoryPriceResponse;
import com.github.yeokyeong_yoon.brand_coordinate_api.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
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
public class CategoryService {

    private final ProductRepository productRepository;

    public CategoryService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * 구현 1) 카테고리별 최저가격 브랜드와 상품 가격, 총액을 조회
     */
    public CategoryLowestPriceResponse findLowestPricesByCategory() {
        List<CategoryLowestPriceResponse.CategoryPrice> categories = new ArrayList<>();
        int totalPrice = 0;

        for (Category category : Category.values()) {
            List<Product> products = productRepository.findByCategory(category);
            if (products.isEmpty()) {
                throw new IllegalArgumentException("해당 카테고리의 상품이 없습니다: " + category);
            }

            // 최저가 찾기
            int lowestPrice = products.stream()
                    .mapToInt(Product::getPrice)
                    .min()
                    .orElseThrow();

            // 최저가인 브랜드들 찾기
            List<CategoryLowestPriceResponse.CategoryPrice.BrandPrice> brandPrices = products.stream()
                    .filter(p -> p.getPrice() == lowestPrice)
                    .map(p -> new CategoryLowestPriceResponse.CategoryPrice.BrandPrice(
                            p.getBrand().getName(),
                            p.getPrice()
                    ))
                    .toList();

            categories.add(new CategoryLowestPriceResponse.CategoryPrice(
                    category.name(),
                    brandPrices
            ));

            totalPrice += lowestPrice;
        }

        return new CategoryLowestPriceResponse(categories, totalPrice);
    }

    /**
     * 구현 3) 카테고리 이름으로 최저, 최고 가격 브랜드와 상품 가격을 조회
     */
    public CategoryPriceResponse findPriceRangeByCategory(Category category) {
        List<Product> products = productRepository.findByCategory(category);
        if (products.isEmpty()) {
            throw new IllegalArgumentException("해당 카테고리의 상품이 없습니다: " + category);
        }

        // 최저가와 최고가 찾기
        int lowestPrice = products.stream()
                .mapToInt(Product::getPrice)
                .min()
                .orElseThrow();

        int highestPrice = products.stream()
                .mapToInt(Product::getPrice)
                .max()
                .orElseThrow();

        // 최저가인 브랜드들 찾기
        List<CategoryPriceResponse.BrandPrice> lowestPrices = products.stream()
                .filter(p -> p.getPrice() == lowestPrice)
                .map(p -> new CategoryPriceResponse.BrandPrice(
                        p.getBrand().getName(),
                        p.getPrice()
                ))
                .toList();

        // 최고가인 브랜드들 찾기
        List<CategoryPriceResponse.BrandPrice> highestPrices = products.stream()
                .filter(p -> p.getPrice() == highestPrice)
                .map(p -> new CategoryPriceResponse.BrandPrice(
                        p.getBrand().getName(),
                        p.getPrice()
                ))
                .toList();

        return new CategoryPriceResponse(
                category.name(),
                lowestPrices,
                highestPrices
        );
    }
} 