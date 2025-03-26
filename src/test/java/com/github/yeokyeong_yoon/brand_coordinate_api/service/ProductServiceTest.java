package com.github.yeokyeong_yoon.brand_coordinate_api.service;

import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Brand;
import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Category;
import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Product;
import com.github.yeokyeong_yoon.brand_coordinate_api.dto.CheapestByCategoryResponse;
import com.github.yeokyeong_yoon.brand_coordinate_api.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Brand brandA;
    private Brand brandB;

    @BeforeEach
    void setUp() {
        brandA = new Brand();
        brandA.setName("A");

        brandB = new Brand();
        brandB.setName("B");
    }

    @Test
    void getCheapestByCategory_ShouldReturnCheapestProductForEachCategory() {
        for (Category category : Category.values()) {
            Product product1 = new Product();
            product1.setBrand(brandA);
            product1.setCategory(category);
            product1.setPrice(10000);

            Product product2 = new Product();
            product2.setBrand(brandA);
            product2.setCategory(category); 
            product2.setPrice(12000);

            Product product3 = new Product();
            product3.setBrand(brandB);
            product3.setCategory(category);
            product3.setPrice(8000);

            Product product4 = new Product();
            product4.setBrand(brandB);
            product4.setCategory(category);
            product4.setPrice(9000);

            Product product5 = new Product();
            product5.setBrand(brandB);
            product5.setCategory(category);
            product5.setPrice(7000);

            when(productRepository.findByCategory(category))
                    .thenReturn(Arrays.asList(product1, product2, product3, product4, product5));
        }

        // When
        CheapestByCategoryResponse response = productService.getCheapestByCategory();

        // Then
        assertThat(response).isNotNull();
        assertThat(response.results()).hasSize(Category.values().length);
        assertThat(response.total()).isEqualTo(7000 * Category.values().length);

        response.results().forEach(result -> {
            assertThat(result.brand()).isEqualTo("B");
            assertThat(result.price()).isEqualTo(7000);
        });
    }

    @Test
    void getCheapestByCategory_WhenNoProducts_ShouldThrowException() {
        // Given
        when(productRepository.findByCategory(Category.TOP))
                .thenReturn(List.of());

        // When & Then
        assertThat(assertThrows(IllegalArgumentException.class,
                () -> productService.getCheapestByCategory()))
                .hasMessageContaining("No product found for category");
    }
} 