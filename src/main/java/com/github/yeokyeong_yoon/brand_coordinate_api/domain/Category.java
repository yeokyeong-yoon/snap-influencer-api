package com.github.yeokyeong_yoon.brand_coordinate_api.domain;

import org.springframework.util.StringUtils;

public enum Category {
    TOP,
    OUTER,
    PANTS,
    SNEAKERS,
    BAG,
    HAT,
    SOCKS,
    ACCESSORY;

    @Override
    public String toString() {
        return StringUtils.capitalize(name());
    }
}