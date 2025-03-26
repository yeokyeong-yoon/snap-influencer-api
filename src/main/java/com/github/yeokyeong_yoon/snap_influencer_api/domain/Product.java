package com.github.yeokyeong_yoon.snap_influencer_api.domain;

import jakarta.persistence.*;

@Entity
public class Product {
    @Id @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    private Category category;

    private int price;

    @ManyToOne
    @JoinColumn(name = "brand_name")
    private Brand brand;
}