package com.github.yeokyeong_yoon.brand_coordinate_api.service;

import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Brand;
import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Category;
import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Product;
import com.github.yeokyeong_yoon.brand_coordinate_api.dto.CategoryPriceResponse;
import com.github.yeokyeong_yoon.brand_coordinate_api.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Brand brandA;
    private Brand brandB;
    private Brand brandC;

    @BeforeEach
    void setUp() {
        brandA = createBrand("A");
        brandB = createBrand("B");
        brandC = createBrand("C");
    }

    // Helper methods for creating test data
    private Brand createBrand(String name) {
        Brand brand = new Brand();
        brand.setName(name);
        return brand;
    }

    private Product createProduct(Brand brand, Category category, int price) {
        Product product = new Product();
        product.setBrand(brand);
        product.setCategory(category);
        product.setPrice(price);
        return product;
    }

    private List<Product> createProductList(Map<Brand, Integer> prices) {
        return prices.entrySet().stream()
                .map(entry -> createProduct(entry.getKey(), Category.TOP, entry.getValue()))
                .toList();
    }

    @Test
    void getPriceRangeByCategory_ShouldReturnCheapestAndMostExpensiveProducts() {
        // Given
        Brand brandA = new Brand();
        brandA.setName("A");

        Product cheapTop = new Product();
        cheapTop.setBrand(brandA);
        cheapTop.setCategory(Category.TOP);
        cheapTop.setPrice(5000);

        Product expensiveTop = new Product();
        expensiveTop.setBrand(brandA);
        expensiveTop.setCategory(Category.TOP);
        expensiveTop.setPrice(10000);

        when(productRepository.findByCategory(Category.TOP))
                .thenReturn(Arrays.asList(cheapTop, expensiveTop));

        // When
        CategoryPriceResponse response = categoryService.findPriceRangeByCategory(Category.TOP);

        // Then
        assertThat(response.category()).isEqualTo(Category.TOP.name());
        assertThat(response.lowestPrices()).hasSize(1);
        assertThat(response.lowestPrices().get(0).brand()).isEqualTo("A");
        assertThat(response.lowestPrices().get(0).price()).isEqualTo(5000);
        assertThat(response.highestPrices()).hasSize(1);
        assertThat(response.highestPrices().get(0).brand()).isEqualTo("A");
        assertThat(response.highestPrices().get(0).price()).isEqualTo(10000);
    }

    @Test
    void getPriceRangeByCategory_WhenProductsHaveDifferentPrices_ShouldReturnCheapestAndMostExpensive() {
        // Given
        Brand brandA = new Brand();
        brandA.setName("A");
        Brand brandB = new Brand();
        brandB.setName("B");

        Product topA = new Product();
        topA.setBrand(brandA);
        topA.setCategory(Category.TOP);
        topA.setPrice(5000);

        Product topB = new Product();
        topB.setBrand(brandB);
        topB.setCategory(Category.TOP);
        topB.setPrice(10000);

        when(productRepository.findByCategory(Category.TOP))
                .thenReturn(Arrays.asList(topA, topB));

        // When
        CategoryPriceResponse response = categoryService.findPriceRangeByCategory(Category.TOP);

        // Then
        assertThat(response.category()).isEqualTo(Category.TOP.name());
        assertThat(response.lowestPrices()).hasSize(1);
        assertThat(response.lowestPrices().get(0).brand()).isEqualTo("A");
        assertThat(response.lowestPrices().get(0).price()).isEqualTo(5000);
        assertThat(response.highestPrices()).hasSize(1);
        assertThat(response.highestPrices().get(0).brand()).isEqualTo("B");
        assertThat(response.highestPrices().get(0).price()).isEqualTo(10000);
    }

    @Test
    void getPriceRangeByCategory_WhenNoProducts_ShouldThrowException() {
        // Given
        when(productRepository.findByCategory(Category.TOP))
                .thenReturn(List.of());

        // When & Then
        assertThrows(IllegalArgumentException.class,
                () -> categoryService.findPriceRangeByCategory(Category.TOP));
    }
} 