package com.github.yeokyeong_yoon.brand_coordinate_api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record CheapestBrandResponse(
    @JsonProperty("최저가_브랜드들") List<BrandTotal> brandTotals
) {
    public record BrandTotal(
        @JsonProperty("브랜드") String brand,
        @JsonProperty("총액") int totalPrice,
        @JsonProperty("카테고리별_가격") List<CategoryPrice> categories
    ) {
        public record CategoryPrice(
            @JsonProperty("카테고리") String category,
            @JsonProperty("가격") int price
        ) {}
    }
} 