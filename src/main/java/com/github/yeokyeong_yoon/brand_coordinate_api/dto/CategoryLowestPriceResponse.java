package com.github.yeokyeong_yoon.brand_coordinate_api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record CategoryLowestPriceResponse(
        List<CategoryPrice> categories,
        int totalPrice
) {

    public record CategoryPrice(
            String category,
            List<BrandPrice> brandPrices
    ) {

        public record BrandPrice(
                String brand,
                int price
        ) {

        }
    }
} 
