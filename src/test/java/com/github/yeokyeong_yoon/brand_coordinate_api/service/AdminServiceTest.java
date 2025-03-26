package com.github.yeokyeong_yoon.brand_coordinate_api.service;

import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Brand;
import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Category;
import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Product;
import com.github.yeokyeong_yoon.brand_coordinate_api.dto.BrandRequest;
import com.github.yeokyeong_yoon.brand_coordinate_api.dto.ProductRequest;
import com.github.yeokyeong_yoon.brand_coordinate_api.repository.BrandRepository;
import com.github.yeokyeong_yoon.brand_coordinate_api.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private BrandRepository brandRepository;
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private AdminService adminService;

    private Brand brandA;
    private Brand brandB;
    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        brandA = new Brand();
        brandA.setId(1L);
        brandA.setName("A");

        brandB = new Brand();
        brandB.setId(2L);
        brandB.setName("B");

        product1 = new Product();
        product1.setId(1L);
        product1.setBrand(brandA);
        product1.setCategory(Category.TOP);
        product1.setPrice(10000);

        product2 = new Product();
        product2.setId(2L);
        product2.setBrand(brandB);
        product2.setCategory(Category.PANTS);
        product2.setPrice(20000);
    }

    @Test
    void registerBrand_ShouldCreateNewBrand() {
        // Given
        BrandRequest request = new BrandRequest("C");
        Brand newBrand = new Brand();
        newBrand.setId(3L);
        newBrand.setName("C");

        when(brandRepository.existsByName("C")).thenReturn(false);
        when(brandRepository.save(any(Brand.class))).thenReturn(newBrand);

        // When
        Brand result = adminService.registerBrand(request);

        // Then
        assertThat(result.getName()).isEqualTo("C");
    }

    @Test
    void registerBrand_WhenBrandExists_ShouldThrowException() {
        // Given
        BrandRequest request = new BrandRequest("A");
        when(brandRepository.existsByName("A")).thenReturn(true);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> adminService.registerBrand(request));
    }

    @Test
    void updateBrand_ShouldUpdateBrandName() {
        // Given
        BrandRequest request = new BrandRequest("NewName");
        when(brandRepository.findById(1L)).thenReturn(Optional.of(brandA));
        when(brandRepository.existsByName("NewName")).thenReturn(false);
        when(brandRepository.save(any(Brand.class))).thenReturn(brandA);

        // When
        Brand result = adminService.updateBrand(1L, request);

        // Then
        assertThat(result.getName()).isEqualTo("NewName");
    }

    @Test
    void updateBrand_WhenBrandNotFound_ShouldThrowException() {
        // Given
        BrandRequest request = new BrandRequest("NewName");
        when(brandRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> adminService.updateBrand(1L, request));
    }

    @Test
    void deleteBrand_ShouldDeleteBrand() {
        // Given
        when(brandRepository.existsById(1L)).thenReturn(true);

        // When & Then
        adminService.deleteBrand(1L);
        // No exception should be thrown
    }

    @Test
    void deleteBrand_WhenBrandNotFound_ShouldThrowException() {
        // Given
        when(brandRepository.existsById(1L)).thenReturn(false);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> adminService.deleteBrand(1L));
    }

    @Test
    void registerProduct_ShouldCreateNewProduct() {
        // Given
        ProductRequest request = new ProductRequest("A", Category.SNEAKERS, 30000);
        when(brandRepository.findByName("A")).thenReturn(Optional.of(brandA));
        when(productRepository.save(any(Product.class))).thenReturn(product1);

        // When
        Product result = adminService.registerProduct(request);

        // Then
        assertThat(result.getBrand().getName()).isEqualTo("A");
        assertThat(result.getCategory()).isEqualTo(Category.SNEAKERS);
        assertThat(result.getPrice()).isEqualTo(30000);
    }

    @Test
    void registerProduct_WhenBrandNotFound_ShouldThrowException() {
        // Given
        ProductRequest request = new ProductRequest("NonExistent", Category.SNEAKERS, 30000);
        when(brandRepository.findByName("NonExistent")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> adminService.registerProduct(request));
    }

    @Test
    void updateProduct_ShouldUpdateProduct() {
        // Given
        ProductRequest request = new ProductRequest("B", Category.OUTER, 25000);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(brandRepository.findByName("B")).thenReturn(Optional.of(brandB));
        when(productRepository.save(any(Product.class))).thenReturn(product1);

        // When
        Product result = adminService.updateProduct(1L, request);

        // Then
        assertThat(result.getBrand().getName()).isEqualTo("B");
        assertThat(result.getCategory()).isEqualTo(Category.OUTER);
        assertThat(result.getPrice()).isEqualTo(25000);
    }

    @Test
    void updateProduct_WhenProductNotFound_ShouldThrowException() {
        // Given
        ProductRequest request = new ProductRequest("B", Category.OUTER, 25000);
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> adminService.updateProduct(1L, request));
    }

    @Test
    void deleteProduct_ShouldDeleteProduct() {
        // Given
        when(productRepository.existsById(1L)).thenReturn(true);

        // When & Then
        adminService.deleteProduct(1L);
        // No exception should be thrown
    }

    @Test
    void deleteProduct_WhenProductNotFound_ShouldThrowException() {
        // Given
        when(productRepository.existsById(1L)).thenReturn(false);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> adminService.deleteProduct(1L));
    }

    @Test
    void getAllBrands_ShouldReturnAllBrands() {
        // Given
        List<Brand> brands = Arrays.asList(brandA, brandB);
        when(brandRepository.findAll()).thenReturn(brands);

        // When
        List<Brand> result = adminService.getAllBrands();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder(brandA, brandB);
    }

    @Test
    void getAllProducts_ShouldReturnAllProducts() {
        // Given
        List<Product> products = Arrays.asList(product1, product2);
        when(productRepository.findAll()).thenReturn(products);

        // When
        List<Product> result = adminService.getAllProducts();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder(product1, product2);
    }
} 