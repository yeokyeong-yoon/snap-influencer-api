package com.github.yeokyeong_yoon.brand_coordinate_api.dto;

import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Category;
import java.util.List;

public record CheapestBrandRequest(
        List<Category> categories
) {

}
