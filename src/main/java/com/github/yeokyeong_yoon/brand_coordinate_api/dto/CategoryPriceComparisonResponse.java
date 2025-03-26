package com.github.yeokyeong_yoon.brand_coordinate_api.dto;

import java.util.List;

public record CategoryPriceComparisonResponse(
    String category,
    List<ProductPriceDto> cheapestProducts,
    List<ProductPriceDto> mostExpensiveProducts
) {} 