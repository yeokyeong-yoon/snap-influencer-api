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
        // 각 카테고리별로 다른 가격의 상품들
        Product topA = new Product();
        topA.setBrand(brandA);
        topA.setCategory(Category.TOP);
        topA.setPrice(10000);

        Product topB = new Product();
        topB.setBrand(brandB);
        topB.setCategory(Category.TOP);
        topB.setPrice(8000);

        Product outerA = new Product();
        outerA.setBrand(brandA);
        outerA.setCategory(Category.OUTER);
        outerA.setPrice(15000);

        Product outerB = new Product();
        outerB.setBrand(brandB);
        outerB.setCategory(Category.OUTER);
        outerB.setPrice(12000);

        Product pantsA = new Product();
        pantsA.setBrand(brandA);
        pantsA.setCategory(Category.PANTS);
        pantsA.setPrice(9000);

        Product pantsB = new Product();
        pantsB.setBrand(brandB);
        pantsB.setCategory(Category.PANTS);
        pantsB.setPrice(7000);

        Product sneakersA = new Product();
        sneakersA.setBrand(brandA);
        sneakersA.setCategory(Category.SNEAKERS);
        sneakersA.setPrice(20000);

        Product sneakersB = new Product();
        sneakersB.setBrand(brandB);
        sneakersB.setCategory(Category.SNEAKERS);
        sneakersB.setPrice(18000);

        Product bagA = new Product();
        bagA.setBrand(brandA);
        bagA.setCategory(Category.BAG);
        bagA.setPrice(25000);

        Product bagB = new Product();
        bagB.setBrand(brandB);
        bagB.setCategory(Category.BAG);
        bagB.setPrice(22000);

        Product hatA = new Product();
        hatA.setBrand(brandA);
        hatA.setCategory(Category.HAT);
        hatA.setPrice(8000);

        Product hatB = new Product();
        hatB.setBrand(brandB);
        hatB.setCategory(Category.HAT);
        hatB.setPrice(6000);

        Product socksA = new Product();
        socksA.setBrand(brandA);
        socksA.setCategory(Category.SOCKS);
        socksA.setPrice(3000);

        Product socksB = new Product();
        socksB.setBrand(brandB);
        socksB.setCategory(Category.SOCKS);
        socksB.setPrice(2000);

        Product accessoryA = new Product();
        accessoryA.setBrand(brandA);
        accessoryA.setCategory(Category.ACCESSORY);
        accessoryA.setPrice(5000);

        Product accessoryB = new Product();
        accessoryB.setBrand(brandB);
        accessoryB.setCategory(Category.ACCESSORY);
        accessoryB.setPrice(4000);

        when(productRepository.findByCategory(Category.TOP))
                .thenReturn(Arrays.asList(topA, topB));
        when(productRepository.findByCategory(Category.OUTER))
                .thenReturn(Arrays.asList(outerA, outerB));
        when(productRepository.findByCategory(Category.PANTS))
                .thenReturn(Arrays.asList(pantsA, pantsB));
        when(productRepository.findByCategory(Category.SNEAKERS))
                .thenReturn(Arrays.asList(sneakersA, sneakersB));
        when(productRepository.findByCategory(Category.BAG))
                .thenReturn(Arrays.asList(bagA, bagB));
        when(productRepository.findByCategory(Category.HAT))
                .thenReturn(Arrays.asList(hatA, hatB));
        when(productRepository.findByCategory(Category.SOCKS))
                .thenReturn(Arrays.asList(socksA, socksB));
        when(productRepository.findByCategory(Category.ACCESSORY))
                .thenReturn(Arrays.asList(accessoryA, accessoryB));

        // When
        CheapestByCategoryResponse response = productService.getCheapestByCategory();

        // Then
        assertThat(response).isNotNull();
        assertThat(response.results()).hasSize(Category.values().length);

        // 각 카테고리별 최저가 브랜드와 가격 검증
        assertThat(response.results().get(0).category()).isEqualTo("TOP");
        assertThat(response.results().get(0).brand()).isEqualTo("B");
        assertThat(response.results().get(0).price()).isEqualTo(8000);

        assertThat(response.results().get(1).category()).isEqualTo("OUTER");
        assertThat(response.results().get(1).brand()).isEqualTo("B");
        assertThat(response.results().get(1).price()).isEqualTo(12000);

        assertThat(response.results().get(2).category()).isEqualTo("PANTS");
        assertThat(response.results().get(2).brand()).isEqualTo("B");
        assertThat(response.results().get(2).price()).isEqualTo(7000);

        assertThat(response.results().get(3).category()).isEqualTo("SNEAKERS");
        assertThat(response.results().get(3).brand()).isEqualTo("B");
        assertThat(response.results().get(3).price()).isEqualTo(18000);

        assertThat(response.results().get(4).category()).isEqualTo("BAG");
        assertThat(response.results().get(4).brand()).isEqualTo("B");
        assertThat(response.results().get(4).price()).isEqualTo(22000);

        assertThat(response.results().get(5).category()).isEqualTo("HAT");
        assertThat(response.results().get(5).brand()).isEqualTo("B");
        assertThat(response.results().get(5).price()).isEqualTo(6000);

        assertThat(response.results().get(6).category()).isEqualTo("SOCKS");
        assertThat(response.results().get(6).brand()).isEqualTo("B");
        assertThat(response.results().get(6).price()).isEqualTo(2000);

        assertThat(response.results().get(7).category()).isEqualTo("ACCESSORY");
        assertThat(response.results().get(7).brand()).isEqualTo("B");
        assertThat(response.results().get(7).price()).isEqualTo(4000);

        // 카테고리별 최저가 상품들의 총액 검증
        // Top - 8000
        // Outer - 12000
        // Pants - 7000
        // Sneakers - 18000
        // Bag - 22000
        // Hat - 6000
        // Socks - 2000
        // Accessory - 4000
        // 총액 검증
        assertThat(response.total()).isEqualTo(8000 + 12000 + 7000 + 18000 + 22000 + 6000 + 2000 + 4000);
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
        // 모든 카테고리가 10000원이므로, 총액은 10000 * 8 = 80000
        assertThat(response.total()).isEqualTo(80000);
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
        // 모든 카테고리가 10000원이므로, 총액은 10000 * 8 = 80000
        assertThat(response.total()).isEqualTo(80000);
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