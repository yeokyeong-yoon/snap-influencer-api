package com.github.yeokyeong_yoon.brand_coordinate_api.dto;

import java.util.List;

public record CategoryPriceResponse(
        String category,
        List<BrandPrice> lowestPrices,
        List<BrandPrice> highestPrices
) {

    public record BrandPrice(
            String brand,
            int price
    ) {

    }
} 
