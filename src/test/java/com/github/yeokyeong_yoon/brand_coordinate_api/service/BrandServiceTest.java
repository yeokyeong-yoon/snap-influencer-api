package com.github.yeokyeong_yoon.brand_coordinate_api.service;

import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Brand;
import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Category;
import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Product;
import com.github.yeokyeong_yoon.brand_coordinate_api.dto.CheapestBrandResponse;
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
    void getCheapestTotalBrand_WhenSingleBrandHasAllCategories_ShouldReturnThatBrand() {
        // Given
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

        when(productRepository.findAll())
                .thenReturn(Arrays.asList(topA, outerA, pantsA, sneakersA, bagA, 
                        hatA, socksA, accessoryA));

        // When
        CheapestBrandResponse response = brandService.findCheapestBrandTotal();

        // Then
        assertThat(response.brandTotals().get(0).brand()).isEqualTo("A");
        assertThat(response.brandTotals().get(0).totalPrice()).isEqualTo(91000);
        assertThat(response.brandTotals().get(0).categories())
            .extracting(CheapestBrandResponse.BrandTotal.CategoryPrice::category)
            .containsExactlyInAnyOrder(
                Category.TOP.name(),
                Category.OUTER.name(),
                Category.PANTS.name(),
                Category.SNEAKERS.name(),
                Category.BAG.name(),
                Category.HAT.name(),
                Category.SOCKS.name(),
                Category.ACCESSORY.name()
            );
    }

    @Test
    void getCheapestTotalBrand_WhenMultipleBrandsHaveAllCategories_ShouldReturnCheapestBrand() {
        // Given
        // Brand A with total price 91000
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

        // Brand B with total price 107000
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

        when(productRepository.findAll())
                .thenReturn(Arrays.asList(
                    topA, outerA, pantsA, sneakersA, bagA, hatA, socksA, accessoryA,
                    topB, outerB, pantsB, sneakersB, bagB, hatB, socksB, accessoryB
                ));

        // When
        CheapestBrandResponse response = brandService.findCheapestBrandTotal();

        // Then
        assertThat(response.brandTotals().get(0).brand()).isEqualTo("A");
        assertThat(response.brandTotals().get(0).totalPrice()).isEqualTo(91000);
    }

    @Test
    void getCheapestTotalBrand_WhenMultipleBrandsHaveEqualTotalPrices_ShouldReturnAllBrands() {
        // Given
        // Both brands with same total price (80000)
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

        when(productRepository.findAll())
                .thenReturn(Arrays.asList(
                    topA, outerA, pantsA, sneakersA, bagA, hatA, socksA, accessoryA,
                    topB, outerB, pantsB, sneakersB, bagB, hatB, socksB, accessoryB
                ));

        // When
        CheapestBrandResponse response = brandService.findCheapestBrandTotal();

        // Then
        assertThat(response.brandTotals().get(0).brand()).isEqualTo("A");
        assertThat(response.brandTotals().get(0).totalPrice()).isEqualTo(80000);
    }

    @Test
    void getCheapestTotalBrand_WhenBrandMissingCategories_ShouldBeFilteredOut() {
        // Given
        // Brand A has all categories
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

        // Brand B is missing some categories
        Product topB = new Product();
        topB.setBrand(brandB);
        topB.setCategory(Category.TOP);
        topB.setPrice(10000);

        when(productRepository.findAll())
                .thenReturn(Arrays.asList(topA, outerA, pantsA, sneakersA, bagA, 
                        hatA, socksA, accessoryA));

        // When
        CheapestBrandResponse response = brandService.findCheapestBrandTotal();

        // Then
        assertThat(response.brandTotals().get(0).brand()).isEqualTo("A");
        assertThat(response.brandTotals().get(0).totalPrice()).isEqualTo(91000);
    }

    @Test
    void getCheapestTotalBrand_WhenAllBrandsMissingCategories_ShouldThrowException() {
        // Given
        when(productRepository.findAll())
                .thenReturn(List.of());

        // When & Then
        assertThrows(IllegalArgumentException.class,
                () -> brandService.findCheapestBrandTotal());
    }

    @Test
    void getCheapestTotalBrand_WhenThreeBrandsWithDifferentPrices_ShouldReturnCheapestBrand() {
        // Given
        // Brand A with total price 91000
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

        // Brand B with total price 107000
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

        // Brand C with total price 95000
        Product topC = new Product();
        topC.setBrand(brandC);
        topC.setCategory(Category.TOP);
        topC.setPrice(8000);

        Product outerC = new Product();
        outerC.setBrand(brandC);
        outerC.setCategory(Category.OUTER);
        outerC.setPrice(18000);

        Product pantsC = new Product();
        pantsC.setBrand(brandC);
        pantsC.setCategory(Category.PANTS);
        pantsC.setPrice(9000);

        Product sneakersC = new Product();
        sneakersC.setBrand(brandC);
        sneakersC.setCategory(Category.SNEAKERS);
        sneakersC.setPrice(22000);

        Product bagC = new Product();
        bagC.setBrand(brandC);
        bagC.setCategory(Category.BAG);
        bagC.setPrice(27000);

        Product hatC = new Product();
        hatC.setBrand(brandC);
        hatC.setCategory(Category.HAT);
        hatC.setPrice(9000);

        Product socksC = new Product();
        socksC.setBrand(brandC);
        socksC.setCategory(Category.SOCKS);
        socksC.setPrice(4000);

        Product accessoryC = new Product();
        accessoryC.setBrand(brandC);
        accessoryC.setCategory(Category.ACCESSORY);
        accessoryC.setPrice(6000);

        when(productRepository.findAll())
                .thenReturn(Arrays.asList(
                    topA, outerA, pantsA, sneakersA, bagA, hatA, socksA, accessoryA,
                    topB, outerB, pantsB, sneakersB, bagB, hatB, socksB, accessoryB,
                    topC, outerC, pantsC, sneakersC, bagC, hatC, socksC, accessoryC
                ));

        // When
        CheapestBrandResponse response = brandService.findCheapestBrandTotal();

        // Then
        assertThat(response.brandTotals().get(0).brand()).isEqualTo("A");
        assertThat(response.brandTotals().get(0).totalPrice()).isEqualTo(91000);
    }
} 