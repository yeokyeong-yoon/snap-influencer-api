package com.github.yeokyeong_yoon.brand_coordinate_api.dto;

import java.util.List;

public record CheapestByBrandResponse(
    List<CheapestProductDto> results,
    int total
) {}
