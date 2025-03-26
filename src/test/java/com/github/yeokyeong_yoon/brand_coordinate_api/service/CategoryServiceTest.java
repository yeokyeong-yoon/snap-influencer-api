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

import java.util.Arrays;
import java.util.List;

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
        brandA = new Brand();
        brandA.setName("A");

        brandB = new Brand();
        brandB.setName("B");

        brandC = new Brand();
        brandC.setName("C");
    }

    @Test
    void comparePricesInCategory_ShouldReturnCheapestAndMostExpensiveProducts() {
        // Given
        Product product1 = new Product();
        product1.setBrand(brandA);
        product1.setCategory(Category.TOP);
        product1.setPrice(10000);

        Product product2 = new Product();
        product2.setBrand(brandB);
        product2.setCategory(Category.TOP);
        product2.setPrice(15000);

        Product product3 = new Product();
        product3.setBrand(brandC);
        product3.setCategory(Category.TOP);
        product3.setPrice(8000);

        when(productRepository.findPriceRangeProductsByCategory(Category.TOP.name()))
                .thenReturn(Arrays.asList(product1, product2, product3));

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
    void comparePricesInCategory_WhenMultipleProductsWithSamePrice_ShouldReturnAll() {
        // Given
        Product product1 = new Product();
        product1.setBrand(brandA);
        product1.setCategory(Category.TOP);
        product1.setPrice(10000);

        Product product2 = new Product();
        product2.setBrand(brandB);
        product2.setCategory(Category.TOP);
        product2.setPrice(10000);

        Product product3 = new Product();
        product3.setBrand(brandC);
        product3.setCategory(Category.TOP);
        product3.setPrice(8000);

        Product product4 = new Product();
        product4.setBrand(brandA);
        product4.setCategory(Category.TOP);
        product4.setPrice(15000);

        Product product5 = new Product();
        product5.setBrand(brandB);
        product5.setCategory(Category.TOP);
        product5.setPrice(15000);

        when(productRepository.findPriceRangeProductsByCategory(Category.TOP.name()))
                .thenReturn(Arrays.asList(product1, product2, product3, product4, product5));

        // When
        CategoryPriceComparisonResponse response = categoryService.comparePricesInCategory(Category.TOP);

        // Then
        assertThat(response.category()).isEqualTo("TOP");
        assertThat(response.cheapestProducts()).hasSize(1);
        assertThat(response.mostExpensiveProducts()).hasSize(2);

        ProductPriceDto cheapest = response.cheapestProducts().get(0);
        assertThat(cheapest.brand()).isEqualTo("C");
        assertThat(cheapest.price()).isEqualTo(8000);

        List<ProductPriceDto> mostExpensive = response.mostExpensiveProducts();
        assertThat(mostExpensive).extracting(ProductPriceDto::brand)
                .containsExactlyInAnyOrder("A", "B");
        assertThat(mostExpensive).extracting(ProductPriceDto::price)
                .containsOnly(15000);
    }

    @Test
    void comparePricesInCategory_WhenNoProducts_ShouldThrowException() {
        // Given
        when(productRepository.findPriceRangeProductsByCategory(Category.TOP.name()))
                .thenReturn(List.of());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> categoryService.comparePricesInCategory(Category.TOP));

        assertThat(exception.getMessage()).isEqualTo("No products found for category: TOP");
    }
} 