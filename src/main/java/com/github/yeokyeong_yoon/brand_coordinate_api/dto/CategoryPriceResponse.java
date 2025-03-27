package com.github.yeokyeong_yoon.brand_coordinate_api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record CategoryPriceResponse(
    @JsonProperty("카테고리") String category,
    @JsonProperty("최저가_브랜드들") List<BrandPrice> lowestPrices,
    @JsonProperty("최고가_브랜드들") List<BrandPrice> highestPrices
) {
    public record BrandPrice(
        @JsonProperty("브랜드") String brand,
        @JsonProperty("가격") int price
    ) {}
} 