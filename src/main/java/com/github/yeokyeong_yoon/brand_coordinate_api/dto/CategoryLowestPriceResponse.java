package com.github.yeokyeong_yoon.brand_coordinate_api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record CategoryLowestPriceResponse(
    @JsonProperty("카테고리별_최저가") List<CategoryPrice> categories,
    @JsonProperty("총액") int totalPrice
) {
    public record CategoryPrice(
        @JsonProperty("카테고리") String category,
        @JsonProperty("최저가_브랜드들") List<BrandPrice> brandPrices
    ) {
        public record BrandPrice(
            @JsonProperty("브랜드") String brand,
            @JsonProperty("가격") int price
        ) {}
    }
} 