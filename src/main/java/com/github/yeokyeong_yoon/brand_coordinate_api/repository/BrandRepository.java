package com.github.yeokyeong_yoon.brand_coordinate_api.repository;

import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BrandRepository extends JpaRepository<Brand, String> {
}