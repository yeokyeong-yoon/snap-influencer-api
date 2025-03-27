package com.github.yeokyeong_yoon.brand_coordinate_api.dto;

import java.util.List;

public sealed interface PriceComparisonResponse permits PriceComparisonResponse.ByBrand, PriceComparisonResponse.ByCategory {
    
    record ByBrand(
        List<ProductPrice> products,
        int total
    ) implements PriceComparisonResponse {}

    record ByCategory(
        String category,
        List<ProductPrice> cheapestProducts,
        List<ProductPrice> mostExpensiveProducts
    ) implements PriceComparisonResponse {}

    record ProductPrice(
        String brand,
        int price
    ) {}
} 