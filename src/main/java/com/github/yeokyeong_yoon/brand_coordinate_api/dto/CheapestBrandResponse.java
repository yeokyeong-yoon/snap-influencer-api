package com.github.yeokyeong_yoon.brand_coordinate_api.dto;

import java.util.List;

public record CheapestBrandResponse(List<BrandTotal> cheapestBrands) {

    public record BrandTotal(
            String brand,
            List<CategoryPrice> categoryPrices,
            int total
    ) {

    }

    public record CategoryPrice(
            String category,
            int price
    ) {

    }
} 
