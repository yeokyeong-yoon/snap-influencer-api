package com.github.yeokyeong_yoon.brand_coordinate_api.service;

import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Category;
import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Product;
import com.github.yeokyeong_yoon.brand_coordinate_api.dto.CategoryPriceComparisonResponse;
import com.github.yeokyeong_yoon.brand_coordinate_api.dto.ProductPriceDto;
import com.github.yeokyeong_yoon.brand_coordinate_api.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public CategoryPriceComparisonResponse comparePricesInCategory(Category category) {
        List<Product> products = productRepository.findPriceRangeProductsByCategory(category.name());
        
        if (products.isEmpty()) {
            throw new IllegalArgumentException("No products found for category: " + category);
        }

        int minPrice = products.stream()
                .mapToInt(Product::getPrice)
                .min()
                .orElseThrow();

        int maxPrice = products.stream()
                .mapToInt(Product::getPrice)
                .max()
                .orElseThrow();

        List<ProductPriceDto> cheapestProducts = products.stream()
                .filter(p -> p.getPrice() == minPrice)
                .map(this::toProductPriceDto)
                .collect(Collectors.toList());

        List<ProductPriceDto> mostExpensiveProducts = products.stream()
                .filter(p -> p.getPrice() == maxPrice)
                .map(this::toProductPriceDto)
                .collect(Collectors.toList());

        return new CategoryPriceComparisonResponse(
                category.name(),
                cheapestProducts,
                mostExpensiveProducts
        );
    }

    private ProductPriceDto toProductPriceDto(Product product) {
        return new ProductPriceDto(
                product.getBrand().getName(),
                product.getPrice()
        );
    }
} 