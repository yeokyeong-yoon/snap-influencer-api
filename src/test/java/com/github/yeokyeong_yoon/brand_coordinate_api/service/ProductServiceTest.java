package com.github.yeokyeong_yoon.brand_coordinate_api.service;

import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Brand;
import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Category;
import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Product;
import com.github.yeokyeong_yoon.brand_coordinate_api.dto.CategoryLowestPriceResponse;
import com.github.yeokyeong_yoon.brand_coordinate_api.dto.CategoryPriceResponse;
import com.github.yeokyeong_yoon.brand_coordinate_api.dto.CheapestBrandResponse;
import com.github.yeokyeong_yoon.brand_coordinate_api.repository.ProductRepository;
import org.assertj.core.api.Assertions;
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
import static org.assertj.core.api.Assertions.tuple;
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
        CategoryLowestPriceResponse response = productService.findLowestPricesByCategory();

        // Then
        assertThat(response).isNotNull();
        assertThat(response.categories()).hasSize(Category.values().length);

        // TOP 카테고리 검증
        var topCategory = response.categories().get(0);
        assertThat(topCategory.category()).isEqualTo("TOP");
        assertThat(topCategory.brandPrices()).hasSize(1);  // 최저가 브랜드만 포함
        
        var topBrandPrice = topCategory.brandPrices().get(0);
        assertThat(topBrandPrice.brand()).isEqualTo("B");
        assertThat(topBrandPrice.price()).isEqualTo(8000);

        // 다른 카테고리들 검증
        var outerCategory = response.categories().get(1);
        assertThat(outerCategory.category()).isEqualTo("OUTER");
        assertThat(outerCategory.brandPrices()).hasSize(1);
        assertThat(outerCategory.brandPrices().get(0).brand()).isEqualTo("B");
        assertThat(outerCategory.brandPrices().get(0).price()).isEqualTo(12000);

        // 나머지 카테고리들도 동일한 패턴으로 검증
        assertThat(response.categories().stream()
                .filter(c -> !c.category().equals("TOP") && !c.category().equals("OUTER"))
                .allMatch(c -> c.brandPrices().size() == 1 &&
                        c.brandPrices().get(0).brand().equals("B"))).isTrue();

        // 총액 검증
        assertThat(response.totalPrice()).isEqualTo(79000); // 8000 + 12000 + 7000 + 18000 + 22000 + 6000 + 2000 + 4000
    }

    @Test
    void findLowestPricesByCategory_WhenNoProducts_ShouldThrowException() {
        // Given
        when(productRepository.findByCategory(Category.TOP))
                .thenReturn(List.of());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> productService.findLowestPricesByCategory());

        assertThat(exception.getMessage()).isEqualTo("해당 카테고리의 상품이 없습니다: TOP");
    }

    @Test
    void findLowestPricesByCategory_WhenMultipleBrandsHaveSamePrice() {
        // Given
        Product topA = new Product();
        topA.setBrand(brandA);
        topA.setCategory(Category.TOP);
        topA.setPrice(8000);  // A와 B가 같은 가격

        Product topB = new Product();
        topB.setBrand(brandB);
        topB.setCategory(Category.TOP);
        topB.setPrice(8000);  // A와 B가 같은 가격

        Product topC = new Product();
        topC.setBrand(brandC);
        topC.setCategory(Category.TOP);
        topC.setPrice(10000);

        when(productRepository.findByCategory(Category.TOP))
                .thenReturn(Arrays.asList(topA, topB, topC));

        // 다른 카테고리도 동일한 패턴으로 설정
        for (Category category : Category.values()) {
            if (category != Category.TOP) {
                Product productA = new Product();
                productA.setBrand(brandA);
                productA.setCategory(category);
                productA.setPrice(5000);

                Product productB = new Product();
                productB.setBrand(brandB);
                productB.setCategory(category);
                productB.setPrice(5000);

                when(productRepository.findByCategory(category))
                        .thenReturn(Arrays.asList(productA, productB));
            }
        }

        // When
        CategoryLowestPriceResponse response = productService.findLowestPricesByCategory();

        // Then
        assertThat(response.categories()).hasSize(Category.values().length);

        // TOP 카테고리 검증
        var topCategory = response.categories().stream()
                .filter(c -> c.category().equals("TOP"))
                .findFirst()
                .orElseThrow();
        assertThat(topCategory.brandPrices()).hasSize(2);  // A와 B 모두 포함

        // 브랜드 A와 B가 모두 포함되어 있는지 검증
        assertThat(topCategory.brandPrices().stream()
                .map(CategoryLowestPriceResponse.CategoryPrice.BrandPrice::brand))
                .containsExactlyInAnyOrder("A", "B");
        
        // 모든 브랜드의 가격이 8000원인지 검증
        assertThat(topCategory.brandPrices().stream()
                .allMatch(bp -> bp.price() == 8000)).isTrue();

        // 다른 카테고리들 검증
        assertThat(response.categories().stream()
                .filter(c -> !c.category().equals("TOP"))
                .allMatch(c -> {
                    var brandPrices = c.brandPrices();
                    return brandPrices.size() == 2 &&  // A와 B 모두 포함
                            brandPrices.stream().allMatch(bp -> bp.price() == 5000) &&  // 모두 5000원
                            brandPrices.stream()
                                    .map(CategoryLowestPriceResponse.CategoryPrice.BrandPrice::brand)
                                    .collect(Collectors.toList())
                                    .containsAll(Arrays.asList("A", "B"));  // A와 B 포함
                })).isTrue();

        // 총액은 각 카테고리의 최저가 합
        assertThat(response.totalPrice()).isEqualTo(8000 + (5000 * (Category.values().length - 1)));
    }

    @Test
    void findCheapestBrandTotal_WhenMultipleBrandsHaveSameTotal() {
        // Given
        Brand brandA = new Brand();
        brandA.setName("A");
        Brand brandB = new Brand();
        brandB.setName("B");

        // Brand A products with total 80000
        Product topA = createProduct(brandA, Category.TOP, 10000);
        Product outerA = createProduct(brandA, Category.OUTER, 10000);
        Product pantsA = createProduct(brandA, Category.PANTS, 10000);
        Product sneakersA = createProduct(brandA, Category.SNEAKERS, 10000);
        Product bagA = createProduct(brandA, Category.BAG, 10000);
        Product hatA = createProduct(brandA, Category.HAT, 10000);
        Product socksA = createProduct(brandA, Category.SOCKS, 10000);
        Product accessoryA = createProduct(brandA, Category.ACCESSORY, 10000);

        // Brand B products with total 80000
        Product topB = createProduct(brandB, Category.TOP, 10000);
        Product outerB = createProduct(brandB, Category.OUTER, 10000);
        Product pantsB = createProduct(brandB, Category.PANTS, 10000);
        Product sneakersB = createProduct(brandB, Category.SNEAKERS, 10000);
        Product bagB = createProduct(brandB, Category.BAG, 10000);
        Product hatB = createProduct(brandB, Category.HAT, 10000);
        Product socksB = createProduct(brandB, Category.SOCKS, 10000);
        Product accessoryB = createProduct(brandB, Category.ACCESSORY, 10000);

        when(productRepository.findAll())
                .thenReturn(Arrays.asList(
                    topA, outerA, pantsA, sneakersA, bagA, hatA, socksA, accessoryA,
                    topB, outerB, pantsB, sneakersB, bagB, hatB, socksB, accessoryB
                ));

        // When
        CheapestBrandResponse response = productService.findCheapestBrandTotal();

        // Then
        assertThat(response.brandTotals()).hasSize(2);
        assertThat(response.brandTotals())
            .extracting(CheapestBrandResponse.BrandTotal::brand)
            .containsExactlyInAnyOrder("A", "B");
        assertThat(response.brandTotals())
            .extracting(CheapestBrandResponse.BrandTotal::totalPrice)
            .containsOnly(80000);

        // Verify that each brand has all categories
        assertThat(response.brandTotals())
            .allMatch(brandTotal -> 
                brandTotal.categories().size() == Category.values().length &&
                brandTotal.categories().stream()
                    .map(CheapestBrandResponse.BrandTotal.CategoryPrice::category)
                    .collect(Collectors.toSet())
                    .containsAll(Arrays.stream(Category.values())
                        .map(Enum::name)
                        .collect(Collectors.toSet()))
            );
    }

    @Test
    void findPriceRangeByCategory_WhenMultipleBrandsHaveSamePrice() {
        // Given
        Product productA = new Product();
        productA.setBrand(brandA);
        productA.setCategory(Category.TOP);
        productA.setPrice(10000);  // A와 B가 최저가

        Product productB = new Product();
        productB.setBrand(brandB);
        productB.setCategory(Category.TOP);
        productB.setPrice(10000);  // A와 B가 최저가

        Product productC = new Product();
        productC.setBrand(brandC);
        productC.setCategory(Category.TOP);
        productC.setPrice(15000);  // C가 최고가

        when(productRepository.findByCategory(Category.TOP))
                .thenReturn(Arrays.asList(productA, productB, productC));

        // When
        CategoryPriceResponse response = productService.findPriceRangeByCategory(Category.TOP);

        // Then
        assertThat(response.category()).isEqualTo("TOP");
        
        // 최저가 브랜드들 검증
        assertThat(response.lowestPrices()).hasSize(2);  // A와 B 모두 포함
        
        // 최저가 브랜드들의 가격이 모두 10000원인지 검증
        assertThat(response.lowestPrices().stream()
                .allMatch(bp -> bp.price() == 10000)).isTrue();

        // 최저가 브랜드들이 A와 B를 포함하는지 검증
        assertThat(response.lowestPrices().stream()
                .map(CategoryPriceResponse.BrandPrice::brand))
                .containsExactlyInAnyOrder("A", "B");

        // 최고가 브랜드 검증
        assertThat(response.highestPrices()).hasSize(1);  // C만 포함
        assertThat(response.highestPrices().stream()
                .allMatch(bp -> bp.brand().equals("C") && bp.price() == 15000)).isTrue();
    }

    private Product createProduct(Brand brand, Category category, int price) {
        Product product = new Product();
        product.setBrand(brand);
        product.setCategory(category);
        product.setPrice(price);
        return product;
    }
}