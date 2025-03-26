package com.github.yeokyeong_yoon.brand_coordinate_api.domain;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Brand {
    @Id
    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "brand", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products = new ArrayList<>();
}