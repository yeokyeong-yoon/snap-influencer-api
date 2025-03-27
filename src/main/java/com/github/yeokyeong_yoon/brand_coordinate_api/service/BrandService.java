package com.github.yeokyeong_yoon.brand_coordinate_api.service;

import java.text.NumberFormat;
import java.util.*;
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
    private final NumberFormat priceFormatter = NumberFormat.getNumberInstance(Locale.KOREA);

    // Frontend 지원: 브랜드 목록 조회
    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }

    /**
     * 구현 2) 단일 브랜드로 모든 카테고리 상품을 구매할 때 최저가격에 판매하는 브랜드와 카테고리의 상품가격, 총액을 조회
     */
    public CheapestBrandResponse getCheapestTotalBrand() {
        List<Brand> brands = brandRepository.findAll();
        Brand cheapestBrand = null;
        int minTotal = Integer.MAX_VALUE;
        Map<Category, Product> cheapestProducts = new HashMap<>();

        for (Brand brand : brands) {
            List<Product> products = productRepository.findByBrand(brand);
            Map<Category, Product> categoryProducts = new HashMap<>();
            
            // Check if brand has products in all categories
            boolean hasAllCategories = true;
            int total = 0;

            for (Category category : Category.values()) {
                Optional<Product> cheapest = products.stream()
                    .filter(p -> p.getCategory() == category)
                    .min(Comparator.comparingInt(Product::getPrice));
                
                if (cheapest.isEmpty()) {
                    hasAllCategories = false;
                    break;
                }

                categoryProducts.put(category, cheapest.get());
                total += cheapest.get().getPrice();
            }

            if (hasAllCategories && total < minTotal) {
                minTotal = total;
                cheapestBrand = brand;
                cheapestProducts = categoryProducts;
            }
        }

        if (cheapestBrand == null) {
            throw new IllegalStateException("No brand has products in all categories");
        }

        List<CheapestBrandResponse.CheapestBrand.CategoryPrice> categoryPrices = cheapestProducts.entrySet().stream()
            .map(entry -> new CheapestBrandResponse.CheapestBrand.CategoryPrice(
                entry.getKey().name(),
                priceFormatter.format(entry.getValue().getPrice())
            ))
            .sorted(Comparator.comparing(CheapestBrandResponse.CheapestBrand.CategoryPrice::category))
            .collect(Collectors.toList());

        CheapestBrandResponse.CheapestBrand cheapest = new CheapestBrandResponse.CheapestBrand(
            cheapestBrand.getName(),
            categoryPrices,
            priceFormatter.format(minTotal)
        );

        return new CheapestBrandResponse(cheapest);
    }
}