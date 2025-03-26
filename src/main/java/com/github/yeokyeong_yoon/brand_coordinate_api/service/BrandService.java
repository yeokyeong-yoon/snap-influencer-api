package com.github.yeokyeong_yoon.brand_coordinate_api.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Category;
import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Product;
import com.github.yeokyeong_yoon.brand_coordinate_api.dto.CheapestByBrandResponse;
import com.github.yeokyeong_yoon.brand_coordinate_api.dto.CheapestProductDto;
import com.github.yeokyeong_yoon.brand_coordinate_api.repository.BrandRepository;
import com.github.yeokyeong_yoon.brand_coordinate_api.repository.ProductRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BrandService {
    private final BrandRepository brandRepository;
    private final ProductRepository productRepository;

    public List<CheapestByBrandResponse> getCheapestTotalBrand() {
        List<CheapestByBrandResponse> responses = new ArrayList<>();
        
        for (var brand : brandRepository.findAll()) {
            List<Product> products = productRepository.findByBrand(brand);
            List<CheapestProductDto> cheapestProducts = new ArrayList<>();
            int total = 0;
            boolean hasAllCategories = true;

            for (Category category : Category.values()) {
                var cheapest = products.stream()
                    .filter(p -> p.getCategory() == category)
                    .min(Comparator.comparingInt(Product::getPrice));
                
                if (cheapest.isEmpty()) {
                    hasAllCategories = false;
                    break;
                }

                var product = cheapest.get();
                cheapestProducts.add(new CheapestProductDto(
                    category.name(), 
                    brand.getName(), 
                    product.getPrice()
                ));
                total += product.getPrice();
            }

            if (hasAllCategories) {
                responses.add(new CheapestByBrandResponse(cheapestProducts, total));
            }
        }

        if (responses.isEmpty()) {
            throw new IllegalStateException("No brand has products in all categories");
        }

        int minTotal = responses.stream()
            .mapToInt(CheapestByBrandResponse::total)
            .min()
            .getAsInt();

        return responses.stream()
            .filter(r -> r.total() == minTotal)
            .toList();
    }
} 