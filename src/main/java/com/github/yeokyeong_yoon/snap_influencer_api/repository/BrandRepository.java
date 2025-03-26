package com.github.yeokyeong_yoon.snap_influencer_api.repository;

import com.github.yeokyeong_yoon.snap_influencer_api.domain.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BrandRepository extends JpaRepository<Brand, String> {
}