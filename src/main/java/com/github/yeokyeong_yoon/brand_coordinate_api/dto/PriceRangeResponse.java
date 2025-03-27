package com.github.yeokyeong_yoon.brand_coordinate_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PriceRangeResponse {

    private String category;
    private double minPrice;
    private double maxPrice;
} 
