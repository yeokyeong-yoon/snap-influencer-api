package com.github.yeokyeong_yoon.brand_coordinate_api.service;

import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Brand;
import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Category;
import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Product;
import com.github.yeokyeong_yoon.brand_coordinate_api.dto.CategoryLowestPriceResponse;
import com.github.yeokyeong_yoon.brand_coordinate_api.dto.CategoryPriceResponse;
import com.github.yeokyeong_yoon.brand_coordinate_api.dto.CheapestBrandResponse;
import com.github.yeokyeong_yoon.brand_coordinate_api.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
    void findLowestPricesByCategory_ShouldReturnCheapestProductForEachCategory() {
        // Given
        for (Category category : Category.values()) {
            Product productA = createProduct(brandA, category, 10000);
            Product productB = createProduct(brandB, category, 8000);
            when(productRepository.findByCategory(category))
                    .thenReturn(Arrays.asList(productA, productB));
        }

        // When
        CategoryLowestPriceResponse response = productService.findLowestPricesByCategory();

        // Then
        assertThat(response).isNotNull();
        assertThat(response.categories()).hasSize(Category.values().length);
        assertThat(response.totalPrice()).isEqualTo(8000 * Category.values().length);

        // 모든 카테고리가 최저가 브랜드 B를 가지고 있는지 확인
        for (var categoryPrice : response.categories()) {
            assertThat(categoryPrice.brandPrices()).hasSize(1);
            assertThat(categoryPrice.brandPrices().get(0).brand()).isEqualTo("B");
            assertThat(categoryPrice.brandPrices().get(0).price()).isEqualTo(8000);
        }
    }

    @Test
    void findLowestPricesByCategory_WhenNoProducts_ShouldThrowException() {
        // Given
        when(productRepository.findByCategory(Category.TOP))
                .thenReturn(List.of());

        // When & Then
        assertThrows(IllegalArgumentException.class,
                () -> productService.findLowestPricesByCategory());
    }

    @Test
    void findLowestPricesByCategory_WhenMultipleBrandsHaveSamePrice() {
        // Given
        for (Category category : Category.values()) {
            Product productA = createProduct(brandA, category, 8000);
            Product productB = createProduct(brandB, category, 8000);
            Product productC = createProduct(brandC, category, 10000);
            when(productRepository.findByCategory(category))
                    .thenReturn(Arrays.asList(productA, productB, productC));
        }

        // When
        CategoryLowestPriceResponse response = productService.findLowestPricesByCategory();

        // Then
        assertThat(response.categories()).hasSize(Category.values().length);
        assertThat(response.totalPrice()).isEqualTo(8000 * Category.values().length);

        // 모든 카테고리에서 A와 B 브랜드가 동일한 최저가를 가지고 있는지 확인
        for (var categoryPrice : response.categories()) {
            assertThat(categoryPrice.brandPrices()).hasSize(2);
            assertThat(categoryPrice.brandPrices().stream()
                    .map(CategoryLowestPriceResponse.CategoryPrice.BrandPrice::brand))
                    .containsExactlyInAnyOrder("A", "B");
            assertThat(categoryPrice.brandPrices().get(0).price()).isEqualTo(8000);
            assertThat(categoryPrice.brandPrices().get(1).price()).isEqualTo(8000);
        }
    }

    @Test
    void findCheapestBrandTotal_WhenMultipleBrandsHaveSameTotal() {
        // Given
        List<Product> allProducts = Arrays.stream(Category.values())
                .flatMap(category -> Arrays.asList(
                    createProduct(brandA, category, 10000),
                    createProduct(brandB, category, 10000)
                ).stream())
                .collect(Collectors.toList());

        when(productRepository.findAll())
                .thenReturn(allProducts);

        // When
        CheapestBrandResponse response = productService.findCheapestBrandTotal();

        // Then
        assertThat(response.brandTotals()).hasSize(2);
        assertThat(response.brandTotals().get(0).brand()).isEqualTo("A");
        assertThat(response.brandTotals().get(0).totalPrice()).isEqualTo(10000 * Category.values().length);
        assertThat(response.brandTotals().get(1).brand()).isEqualTo("B");
        assertThat(response.brandTotals().get(1).totalPrice()).isEqualTo(10000 * Category.values().length);
    }

    @Test
    void findPriceRangeByCategory_WhenMultipleBrandsHaveSamePrice() {
        // Given
        Product productA = createProduct(brandA, Category.TOP, 10000);
        Product productB = createProduct(brandB, Category.TOP, 10000);
        Product productC = createProduct(brandC, Category.TOP, 15000);

        when(productRepository.findByCategory(Category.TOP))
                .thenReturn(Arrays.asList(productA, productB, productC));

        // When
        CategoryPriceResponse response = productService.findPriceRangeByCategory(Category.TOP);

        // Then
        assertThat(response.category()).isEqualTo("TOP");
        assertThat(response.lowestPrices()).hasSize(2);
        assertThat(response.lowestPrices().get(0).price()).isEqualTo(10000);
        assertThat(response.lowestPrices().get(1).price()).isEqualTo(10000);
        assertThat(response.highestPrices()).hasSize(1);
        assertThat(response.highestPrices().get(0).brand()).isEqualTo("C");
        assertThat(response.highestPrices().get(0).price()).isEqualTo(15000);
    }

    private Product createProduct(Brand brand, Category category, int price) {
        Product product = new Product();
        product.setBrand(brand);
        product.setCategory(category);
        product.setPrice(price);
        return product;
    }
}