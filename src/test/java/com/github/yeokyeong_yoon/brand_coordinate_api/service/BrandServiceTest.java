package com.github.yeokyeong_yoon.brand_coordinate_api.service;

import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Brand;
import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Category;
import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Product;
import com.github.yeokyeong_yoon.brand_coordinate_api.dto.CheapestByBrandResponse;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BrandServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private BrandRepository brandRepository;

    @InjectMocks
    private BrandService brandService;

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
    void getCheapestTotalBrand_ShouldReturnCheapestBrand() {
        // Given
        // Mock products for brand A
        Product topA = new Product();
        topA.setBrand(brandA);
        topA.setCategory(Category.TOP);
        topA.setPrice(5000);

        Product outerA = new Product();
        outerA.setBrand(brandA);
        outerA.setCategory(Category.OUTER);
        outerA.setPrice(15000);

        Product pantsA = new Product();
        pantsA.setBrand(brandA);
        pantsA.setCategory(Category.PANTS);
        pantsA.setPrice(10000);

        Product sneakersA = new Product();
        sneakersA.setBrand(brandA);
        sneakersA.setCategory(Category.SNEAKERS);
        sneakersA.setPrice(20000);

        Product bagA = new Product();
        bagA.setBrand(brandA);
        bagA.setCategory(Category.BAG);
        bagA.setPrice(25000);

        Product hatA = new Product();
        hatA.setBrand(brandA);
        hatA.setCategory(Category.HAT);
        hatA.setPrice(8000);

        Product socksA = new Product();
        socksA.setBrand(brandA);
        socksA.setCategory(Category.SOCKS);
        socksA.setPrice(3000);

        Product accessoryA = new Product();
        accessoryA.setBrand(brandA);
        accessoryA.setCategory(Category.ACCESSORY);
        accessoryA.setPrice(5000);

        // Mock products for brand B
        Product topB = new Product();
        topB.setBrand(brandB);
        topB.setCategory(Category.TOP);
        topB.setPrice(10000);

        Product outerB = new Product();
        outerB.setBrand(brandB);
        outerB.setCategory(Category.OUTER);
        outerB.setPrice(20000);

        Product pantsB = new Product();
        pantsB.setBrand(brandB);
        pantsB.setCategory(Category.PANTS);
        pantsB.setPrice(7000);

        Product sneakersB = new Product();
        sneakersB.setBrand(brandB);
        sneakersB.setCategory(Category.SNEAKERS);
        sneakersB.setPrice(25000);

        Product bagB = new Product();
        bagB.setBrand(brandB);
        bagB.setCategory(Category.BAG);
        bagB.setPrice(30000);

        Product hatB = new Product();
        hatB.setBrand(brandB);
        hatB.setCategory(Category.HAT);
        hatB.setPrice(12000);

        Product socksB = new Product();
        socksB.setBrand(brandB);
        socksB.setCategory(Category.SOCKS);
        socksB.setPrice(5000);

        Product accessoryB = new Product();
        accessoryB.setBrand(brandB);
        accessoryB.setCategory(Category.ACCESSORY);
        accessoryB.setPrice(8000);

        when(productRepository.findByBrand(brandA))
                .thenReturn(Arrays.asList(topA, outerA, pantsA, sneakersA, bagA, 
                        hatA, socksA, accessoryA));

        when(productRepository.findByBrand(brandB))
                .thenReturn(Arrays.asList(topB, outerB, pantsB, sneakersB, bagB,
                        hatB, socksB, accessoryB));

        when(brandRepository.findAll())
                .thenReturn(Arrays.asList(brandA, brandB));

        // When
        List<CheapestByBrandResponse> responses = brandService.getCheapestTotalBrand();

        // Then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).results().get(0).brand()).isEqualTo("A");   
        // Calculate expected total for brand A: 5000 + 15000 + 10000 + 20000 + 25000 + 8000 + 3000 + 5000 = 91000
        // Calculate expected total for brand B: 10000 + 20000 + 7000 + 25000 + 30000 + 12000 + 5000 + 8000 = 107000
        assertThat(responses.get(0).total()).isEqualTo(91000);
    }

    @Test
    void getCheapestTotalBrand_WhenBrandMissingCategories_ShouldBeFilteredOut() {
        // Given
        // Brand A missing some categories
        Product topA = new Product();
        topA.setBrand(brandA);
        topA.setCategory(Category.TOP);
        topA.setPrice(5000);

        Product outerA = new Product();
        outerA.setBrand(brandA);
        outerA.setCategory(Category.OUTER);
        outerA.setPrice(15000);

        // Brand B missing some categories
        Product topB = new Product();
        topB.setBrand(brandB);
        topB.setCategory(Category.TOP);
        topB.setPrice(10000);

        when(productRepository.findByBrand(brandA))
                .thenReturn(Arrays.asList(topA, outerA));

        when(productRepository.findByBrand(brandB))
                .thenReturn(Arrays.asList(topB));

        when(brandRepository.findAll())
                .thenReturn(Arrays.asList(brandA, brandB));

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> brandService.getCheapestTotalBrand());

        assertThat(exception.getMessage()).isEqualTo("No brand has products in all categories");
    }

    @Test
    void getCheapestTotalBrand_WhenAllBrandsMissingCategories_ShouldThrowException() {
        // Given
        // Both brands missing some categories
        Product topA = new Product();
        topA.setBrand(brandA);
        topA.setCategory(Category.TOP);
        topA.setPrice(5000);

        Product topB = new Product();
        topB.setBrand(brandB);
        topB.setCategory(Category.TOP);
        topB.setPrice(10000);

        when(productRepository.findByBrand(brandA))
                .thenReturn(Arrays.asList(topA));

        when(productRepository.findByBrand(brandB))
                .thenReturn(Arrays.asList(topB));

        when(brandRepository.findAll())
                .thenReturn(Arrays.asList(brandA, brandB));

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> brandService.getCheapestTotalBrand());

        assertThat(exception.getMessage()).isEqualTo("No brand has products in all categories");
    }

    @Test
    void getCheapestTotalBrand_WhenSingleBrand_ShouldReturnThatBrand() {
        // Given
        // Single brand with all categories
        Product topA = new Product();
        topA.setBrand(brandA);
        topA.setCategory(Category.TOP);
        topA.setPrice(5000);

        Product outerA = new Product();
        outerA.setBrand(brandA);
        outerA.setCategory(Category.OUTER);
        outerA.setPrice(15000);

        Product pantsA = new Product();
        pantsA.setBrand(brandA);
        pantsA.setCategory(Category.PANTS);
        pantsA.setPrice(10000);

        Product sneakersA = new Product();
        sneakersA.setBrand(brandA);
        sneakersA.setCategory(Category.SNEAKERS);
        sneakersA.setPrice(20000);

        Product bagA = new Product();
        bagA.setBrand(brandA);
        bagA.setCategory(Category.BAG);
        bagA.setPrice(25000);

        Product hatA = new Product();
        hatA.setBrand(brandA);
        hatA.setCategory(Category.HAT);
        hatA.setPrice(8000);

        Product socksA = new Product();
        socksA.setBrand(brandA);
        socksA.setCategory(Category.SOCKS);
        socksA.setPrice(3000);

        Product accessoryA = new Product();
        accessoryA.setBrand(brandA);
        accessoryA.setCategory(Category.ACCESSORY);
        accessoryA.setPrice(5000);

        when(productRepository.findByBrand(brandA))
                .thenReturn(Arrays.asList(topA, outerA, pantsA, sneakersA, bagA, 
                        hatA, socksA, accessoryA));

        when(brandRepository.findAll())
                .thenReturn(Arrays.asList(brandA));

        // When
        List<CheapestByBrandResponse> responses = brandService.getCheapestTotalBrand();

        // Then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).results().get(0).brand()).isEqualTo("A");
        // Calculate expected total: 5000 + 15000 + 10000 + 20000 + 25000 + 8000 + 3000 + 5000 = 91000
        assertThat(responses.get(0).total()).isEqualTo(91000);
    }

    @Test
    void getCheapestTotalBrand_WhenEqualTotalPrices_ShouldReturnAllBrands() {
        // Given
        // Both brands with same total price
        Product topA = new Product();
        topA.setBrand(brandA);
        topA.setCategory(Category.TOP);
        topA.setPrice(10000);

        Product outerA = new Product();
        outerA.setBrand(brandA);
        outerA.setCategory(Category.OUTER);
        outerA.setPrice(10000);

        Product pantsA = new Product();
        pantsA.setBrand(brandA);
        pantsA.setCategory(Category.PANTS);
        pantsA.setPrice(10000);

        Product sneakersA = new Product();
        sneakersA.setBrand(brandA);
        sneakersA.setCategory(Category.SNEAKERS);
        sneakersA.setPrice(10000);

        Product bagA = new Product();
        bagA.setBrand(brandA);
        bagA.setCategory(Category.BAG);
        bagA.setPrice(10000);

        Product hatA = new Product();
        hatA.setBrand(brandA);
        hatA.setCategory(Category.HAT);
        hatA.setPrice(10000);

        Product socksA = new Product();
        socksA.setBrand(brandA);
        socksA.setCategory(Category.SOCKS);
        socksA.setPrice(10000);

        Product accessoryA = new Product();
        accessoryA.setBrand(brandA);
        accessoryA.setCategory(Category.ACCESSORY);
        accessoryA.setPrice(10000);

        Product topB = new Product();
        topB.setBrand(brandB);
        topB.setCategory(Category.TOP);
        topB.setPrice(10000);

        Product outerB = new Product();
        outerB.setBrand(brandB);
        outerB.setCategory(Category.OUTER);
        outerB.setPrice(10000);

        Product pantsB = new Product();
        pantsB.setBrand(brandB);
        pantsB.setCategory(Category.PANTS);
        pantsB.setPrice(10000);

        Product sneakersB = new Product();
        sneakersB.setBrand(brandB);
        sneakersB.setCategory(Category.SNEAKERS);
        sneakersB.setPrice(10000);

        Product bagB = new Product();
        bagB.setBrand(brandB);
        bagB.setCategory(Category.BAG);
        bagB.setPrice(10000);

        Product hatB = new Product();
        hatB.setBrand(brandB);
        hatB.setCategory(Category.HAT);
        hatB.setPrice(10000);

        Product socksB = new Product();
        socksB.setBrand(brandB);
        socksB.setCategory(Category.SOCKS);
        socksB.setPrice(10000);

        Product accessoryB = new Product();
        accessoryB.setBrand(brandB);
        accessoryB.setCategory(Category.ACCESSORY);
        accessoryB.setPrice(10000);

        when(productRepository.findByBrand(brandA))
                .thenReturn(Arrays.asList(topA, outerA, pantsA, sneakersA, bagA, 
                        hatA, socksA, accessoryA));

        when(productRepository.findByBrand(brandB))
                .thenReturn(Arrays.asList(topB, outerB, pantsB, sneakersB, bagB,
                        hatB, socksB, accessoryB));

        when(brandRepository.findAll())
                .thenReturn(Arrays.asList(brandA, brandB));

        // When
        List<CheapestByBrandResponse> responses = brandService.getCheapestTotalBrand();

        // Then
        assertThat(responses).hasSize(2);
        // Both brands should have the same total price
        assertThat(responses.get(0).total()).isEqualTo(80000);
        assertThat(responses.get(1).total()).isEqualTo(80000);
        // Both brands should be included in the results
        assertThat(responses.stream()
                .map(response -> response.results().get(0).brand())
                .toList())
                .containsExactlyInAnyOrder("A", "B");
    }
} 