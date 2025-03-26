package com.github.yeokyeong_yoon.brand_coordinate_api.dto;

import java.util.List;

public record CheapestByCategoryResponse(
    List<CheapestProductDto> results,
    int total
) {}