package com.github.yeokyeong_yoon.brand_coordinate_api.service;

import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Brand;
import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Category;
import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Product;
import com.github.yeokyeong_yoon.brand_coordinate_api.dto.CategoryPriceComparisonResponse;
import com.github.yeokyeong_yoon.brand_coordinate_api.dto.ProductPriceDto;
import com.github.yeokyeong_yoon.brand_coordinate_api.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    void comparePricesInCategory_ShouldReturnCheapestAndMostExpensiveProducts() {
        // Given
        Map<Brand, Integer> prices = Map.of(
            brandA, 10000,
            brandB, 15000,
            brandC, 8000
        );

        when(productRepository.findPriceRangeProductsByCategory(Category.TOP))
                .thenReturn(createProductList(prices));

        // When
        CategoryPriceComparisonResponse response = categoryService.comparePricesInCategory(Category.TOP);

        // Then
        assertThat(response.category()).isEqualTo("TOP");
        assertThat(response.cheapestProducts()).hasSize(1);
        assertThat(response.mostExpensiveProducts()).hasSize(1);

        ProductPriceDto cheapest = response.cheapestProducts().get(0);
        assertThat(cheapest.brand()).isEqualTo("C");
        assertThat(cheapest.price()).isEqualTo(8000);

        ProductPriceDto mostExpensive = response.mostExpensiveProducts().get(0);
        assertThat(mostExpensive.brand()).isEqualTo("B");
        assertThat(mostExpensive.price()).isEqualTo(15000);
    }

    @Test
    void comparePricesInCategory_WhenProductsHaveDifferentPrices_ShouldReturnCheapestAndMostExpensive() {
        // Given
        Map<Brand, Integer> prices = Map.of(
            brandA, 10000,
            brandB, 15000,
            brandC, 8000
        );

        when(productRepository.findPriceRangeProductsByCategory(Category.TOP))
                .thenReturn(createProductList(prices));

        // When
        CategoryPriceComparisonResponse response = categoryService.comparePricesInCategory(Category.TOP);

        // Then
        assertThat(response.category()).isEqualTo("TOP");
        
        // Check cheapest products
        List<ProductPriceDto> cheapestProducts = response.cheapestProducts();
        assertThat(cheapestProducts).hasSize(1);
        assertThat(cheapestProducts.get(0).brand()).isEqualTo("C");
        assertThat(cheapestProducts.get(0).price()).isEqualTo(8000);

        // Check most expensive products
        List<ProductPriceDto> mostExpensiveProducts = response.mostExpensiveProducts();
        assertThat(mostExpensiveProducts).hasSize(1);
        assertThat(mostExpensiveProducts.get(0).brand()).isEqualTo("B");
        assertThat(mostExpensiveProducts.get(0).price()).isEqualTo(15000);
    }

    @Test
    void comparePricesInCategory_WhenAllProductsHaveSamePrice_ShouldReturnAll() {
        // Given
        Map<Brand, Integer> prices = Map.of(
            brandA, 10000,
            brandB, 10000,
            brandC, 10000
        );

        when(productRepository.findPriceRangeProductsByCategory(Category.TOP))
                .thenReturn(createProductList(prices));

        // When
        CategoryPriceComparisonResponse response = categoryService.comparePricesInCategory(Category.TOP);

        // Then
        assertThat(response.category()).isEqualTo("TOP");
        
        // Check that all products are in both cheapest and most expensive lists
        List<ProductPriceDto> cheapestProducts = response.cheapestProducts();
        List<ProductPriceDto> mostExpensiveProducts = response.mostExpensiveProducts();
        
        assertThat(cheapestProducts).hasSize(3);
        assertThat(mostExpensiveProducts).hasSize(3);
        
        // Verify all products have the same price
        assertThat(cheapestProducts).extracting(ProductPriceDto::price)
                .containsOnly(10000);
        assertThat(mostExpensiveProducts).extracting(ProductPriceDto::price)
                .containsOnly(10000);
        
        // Verify all brands are included
        assertThat(cheapestProducts).extracting(ProductPriceDto::brand)
                .containsExactlyInAnyOrder("A", "B", "C");
        assertThat(mostExpensiveProducts).extracting(ProductPriceDto::brand)
                .containsExactlyInAnyOrder("A", "B", "C");
    }

    @Test
    void comparePricesInCategory_WhenNoProducts_ShouldThrowException() {
        // Given
        when(productRepository.findPriceRangeProductsByCategory(Category.TOP))
                .thenReturn(List.of());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> categoryService.comparePricesInCategory(Category.TOP));

        assertThat(exception.getMessage()).isEqualTo("No products found for category: TOP");
    }
} 