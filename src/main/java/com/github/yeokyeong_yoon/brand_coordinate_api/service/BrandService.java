package com.github.yeokyeong_yoon.brand_coordinate_api.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Brand;
import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Product;
import com.github.yeokyeong_yoon.brand_coordinate_api.dto.PriceComparisonResponse;
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

    /**
     * 모든 카테고리의 상품을 가진 브랜드 중에서, 
     * 각 카테고리별 최저가 상품의 총합이 가장 작은 브랜드를 찾습니다.
     */
    public List<PriceComparisonResponse.ByBrand> getCheapestTotalBrand() {
        List<Brand> brands = brandRepository.findAll();
        Map<Brand, List<Product>> productsByBrand = brands.stream()
                .collect(Collectors.toMap(
                        brand -> brand,
                        brand -> productRepository.findByBrand(brand)
                ));

        return productsByBrand.entrySet().stream()
                .map(entry -> {
                    Brand brand = entry.getKey();
                    List<Product> products = entry.getValue();
                    int totalPrice = products.stream()
                            .mapToInt(Product::getPrice)
                            .sum();
                    return new PriceComparisonResponse.ByBrand(
                            List.of(new PriceComparisonResponse.ProductPrice(brand.getName(), totalPrice)),
                            products.size()
                    );
                })
                .collect(Collectors.toList());
    }
}