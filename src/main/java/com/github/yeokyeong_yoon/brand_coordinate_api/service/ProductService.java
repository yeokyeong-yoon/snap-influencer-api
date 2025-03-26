package com.github.yeokyeong_yoon.brand_coordinate_api.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Brand;
import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Category;
import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Product;
import com.github.yeokyeong_yoon.brand_coordinate_api.dto.CheapestByBrandResponse;
import com.github.yeokyeong_yoon.brand_coordinate_api.dto.CheapestByCategoryResponse;
import com.github.yeokyeong_yoon.brand_coordinate_api.dto.CheapestProductDto;
import com.github.yeokyeong_yoon.brand_coordinate_api.repository.BrandRepository;
import com.github.yeokyeong_yoon.brand_coordinate_api.repository.ProductRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;

    public CheapestByCategoryResponse getCheapestByCategory() {
        List<CheapestProductDto> results = new ArrayList<>();
        int total = 0;

        for (Category category : Category.values()) {
            Product cheapest = productRepository.findByCategory(category).stream()
                    .min(Comparator.comparingInt(Product::getPrice))
                    .orElseThrow(() -> new IllegalArgumentException("No product found for category: " + category));
            results.add(new CheapestProductDto(category.name(), cheapest.getBrand().getName(), cheapest.getPrice()));
            total += cheapest.getPrice();
        }

        return new CheapestByCategoryResponse(results, total);
    }

    public CheapestByBrandResponse getCheapestTotalBrand() {
        return brandRepository.findAll().stream()
            .map(brand -> {
                try {
                    List<CheapestProductDto> products = new ArrayList<>();
                    int total = 0;
                    
                    for (Category category : Category.values()) {
                        Product cheapest = productRepository.findByBrand(brand).stream()
                            .filter(p -> p.getCategory() == category)
                            .min(Comparator.comparingInt(Product::getPrice))
                            .orElseThrow(() -> new IllegalStateException("Missing category"));
                            
                        products.add(new CheapestProductDto(category.name(), brand.getName(), cheapest.getPrice()));
                        total += cheapest.getPrice();
                    }
                    
                    return new CheapestByBrandResponse(products, total);
                } catch (IllegalStateException e) {
                    return null;
                }
            })
            .filter(response -> response != null)
            .min(Comparator.comparing(CheapestByBrandResponse::total))
            .orElseThrow(() -> new IllegalStateException("No brand has products in all categories"));
    }
}