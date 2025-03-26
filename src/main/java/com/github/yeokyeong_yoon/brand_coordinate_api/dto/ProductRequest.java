package com.github.yeokyeong_yoon.brand_coordinate_api.dto;

import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Category;

public record ProductRequest(
    String brandName,
    Category category,
    int price
) {} 