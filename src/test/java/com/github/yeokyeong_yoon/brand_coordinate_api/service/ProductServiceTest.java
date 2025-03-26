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
        // Given
        for (Category category : Category.values()) {
            Product product1 = new Product();
            product1.setBrand(brandA);
            product1.setCategory(category);
            product1.setPrice(10000);

            Product product2 = new Product();
            product2.setBrand(brandB);
            product2.setCategory(category);
            product2.setPrice(8000);

            when(productRepository.findByCategory(category))
                    .thenReturn(Arrays.asList(product1, product2));
        }

        // When
        CheapestByCategoryResponse response = productService.getCheapestByCategory();

        // Then
        assertThat(response).isNotNull();
        assertThat(response.results()).hasSize(Category.values().length);
        assertThat(response.total()).isEqualTo(8000 * Category.values().length);

        response.results().forEach(result -> {
            assertThat(result.brand()).isEqualTo("B");
            assertThat(result.price()).isEqualTo(8000);
        });
    }

    @Test
    void getCheapestByCategory_WhenSingleProduct_ShouldReturnThatProduct() {
        // Given
        Product singleProduct = new Product();
        singleProduct.setBrand(brandA);
        singleProduct.setCategory(Category.TOP);
        singleProduct.setPrice(10000);

        when(productRepository.findByCategory(Category.TOP))
                .thenReturn(List.of(singleProduct));

        // Mock empty lists for other categories to focus test on TOP
        for (Category category : Category.values()) {
            if (category != Category.TOP) {
                when(productRepository.findByCategory(category))
                        .thenReturn(List.of(singleProduct));
            }
        }

        // When
        CheapestByCategoryResponse response = productService.getCheapestByCategory();

        // Then
        assertThat(response.results().get(0).brand()).isEqualTo("A");
        assertThat(response.results().get(0).price()).isEqualTo(10000);
    }

    @Test
    void getCheapestByCategory_WhenSamePriceProducts_ShouldReturnAny() {
        // Given
        Product product1 = new Product();
        product1.setBrand(brandA);
        product1.setCategory(Category.TOP);
        product1.setPrice(10000);

        Product product2 = new Product();
        product2.setBrand(brandB);
        product2.setCategory(Category.TOP);
        product2.setPrice(10000);

        when(productRepository.findByCategory(Category.TOP))
                .thenReturn(Arrays.asList(product1, product2));

        // Mock data for other categories
        for (Category category : Category.values()) {
            if (category != Category.TOP) {
                when(productRepository.findByCategory(category))
                        .thenReturn(Arrays.asList(product1, product2));
            }
        }

        // When
        CheapestByCategoryResponse response = productService.getCheapestByCategory();

        // Then
        assertThat(response.results().get(0).price()).isEqualTo(10000);
        // Don't assert brand as it could be either A or B
    }

    @Test
    void getCheapestByCategory_WhenNoProducts_ShouldThrowException() {
        // Given - test only TOP category since exception will be thrown immediately
        when(productRepository.findByCategory(Category.TOP))
                .thenReturn(List.of());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> productService.getCheapestByCategory());

        assertThat(exception.getMessage()).isEqualTo("No product found for category: TOP");
    }
}