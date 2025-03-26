package com.github.yeokyeong_yoon.brand_coordinate_api.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Category;
import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Product;
import com.github.yeokyeong_yoon.brand_coordinate_api.dto.CheapestByBrandResponse;
import com.github.yeokyeong_yoon.brand_coordinate_api.dto.CheapestProductDto;
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
public class BrandService {
    private final BrandRepository brandRepository;
    private final ProductRepository productRepository;

    /**
     * 모든 카테고리의 상품을 가진 브랜드 중에서, 
     * 각 카테고리별 최저가 상품의 총합이 가장 작은 브랜드를 찾습니다.
     */
    @Transactional(readOnly = true)
    public List<CheapestByBrandResponse> getCheapestTotalBrand() {
        List<CheapestByBrandResponse> responses = new ArrayList<>();
        
        for (var brand : brandRepository.findAll()) {
            List<Product> products = productRepository.findByBrand(brand);
            if (!hasProductsInAllCategories(products)) {
                continue;
            }

            List<CheapestProductDto> cheapestProducts = new ArrayList<>();
            int total = 0;

            for (Category category : Category.values()) {
                Product cheapestProduct = findCheapestProductInCategory(products, category);
                cheapestProducts.add(new CheapestProductDto(
                    category.name(), 
                    brand.getName(), 
                    cheapestProduct.getPrice()
                ));
                total += cheapestProduct.getPrice();
            }

            responses.add(new CheapestByBrandResponse(cheapestProducts, total));
        }

        if (responses.isEmpty()) {
            throw new IllegalStateException("Failed: No brand has products in all categories");
        }

        return findBrandsWithMinTotal(responses);
    }

    // 브랜드가 모든 카테고리의 상품을 가지고 있는지 확인합니다.
    private boolean hasProductsInAllCategories(List<Product> products) {
        for (Category category : Category.values()) {
            boolean hasCategory = false;
            for (Product product : products) {
                if (product.getCategory() == category) {
                    hasCategory = true;
                    break;
                }
            }
            if (!hasCategory) {
                return false;
            }
        }
        return true;
    }

    // 특정 카테고리에서 가장 싼 상품을 찾습니다.
    private Product findCheapestProductInCategory(List<Product> products, Category category) {
        Product cheapest = null;
        for (Product product : products) {
            if (product.getCategory() == category) {
                if (cheapest == null || product.getPrice() < cheapest.getPrice()) {
                    cheapest = product;
                }
            }
        }
        if (cheapest == null) {
            throw new IllegalStateException("No product found for category: " + category);
        }
        return cheapest;
    }

    // 총 가격이 가장 작은 브랜드들을 찾습니다.
    private List<CheapestByBrandResponse> findBrandsWithMinTotal(List<CheapestByBrandResponse> responses) {
        // 최소 총액 찾기
        int minTotal = Integer.MAX_VALUE;
        for (CheapestByBrandResponse response : responses) {
            if (response.total() < minTotal) {
                minTotal = response.total();
            }
        }

        // 최소 총액을 가진 브랜드들 찾기
        List<CheapestByBrandResponse> result = new ArrayList<>();
        for (CheapestByBrandResponse response : responses) {
            if (response.total() == minTotal) {
                result.add(response);
            }
        }
        return result;
    }
}