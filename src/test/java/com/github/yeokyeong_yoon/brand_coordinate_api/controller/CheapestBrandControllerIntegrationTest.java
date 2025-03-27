package com.github.yeokyeong_yoon.brand_coordinate_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.yeokyeong_yoon.brand_coordinate_api.dto.CheapestBrandRequest;
import com.github.yeokyeong_yoon.brand_coordinate_api.entity.Brand;
import com.github.yeokyeong_yoon.brand_coordinate_api.entity.Category;
import com.github.yeokyeong_yoon.brand_coordinate_api.entity.Product;
import com.github.yeokyeong_yoon.brand_coordinate_api.repository.BrandRepository;
import com.github.yeokyeong_yoon.brand_coordinate_api.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;

@SpringBootTest
@AutoConfigureMockMvc
public class CheapestBrandControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BrandRepository brandRepository;

    private Brand nike;
    private Brand adidas;
    private Brand puma;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        brandRepository.deleteAll();

        nike = brandRepository.save(new Brand("Nike"));
        adidas = brandRepository.save(new Brand("Adidas"));
        puma = brandRepository.save(new Brand("Puma"));

        // Nike products
        productRepository.save(new Product(nike, Category.TOP, 50000));
        productRepository.save(new Product(nike, Category.PANTS, 60000));
        productRepository.save(new Product(nike, Category.SNEAKERS, 90000));

        // Adidas products
        productRepository.save(new Product(adidas, Category.TOP, 45000));
        productRepository.save(new Product(adidas, Category.PANTS, 70000));
        productRepository.save(new Product(adidas, Category.SNEAKERS, 85000));

        // Puma products
        productRepository.save(new Product(puma, Category.TOP, 40000));
        productRepository.save(new Product(puma, Category.PANTS, 65000));
        productRepository.save(new Product(puma, Category.SNEAKERS, 95000));
    }

    @Test
    void findCheapestBrandTotal_Success() throws Exception {
        // given
        List<Category> categories = Arrays.asList(Category.TOP, Category.PANTS, Category.SNEAKERS);
        CheapestBrandRequest request = new CheapestBrandRequest(categories);
        String content = objectMapper.writeValueAsString(request);

        // when & then
        mockMvc.perform(post("/products/cheapest-brand")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.brandTotals[0].brand", is("Adidas")))
                .andExpect(jsonPath("$.data.brandTotals[0].total", is(200000)))  // 45000 + 70000 + 85000
                .andExpect(jsonPath("$.data.selectedCategories[0]", is("TOP")))
                .andExpect(jsonPath("$.data.selectedCategories[1]", is("PANTS")))
                .andExpect(jsonPath("$.data.selectedCategories[2]", is("SNEAKERS")));
    }

    @Test
    void findCheapestBrandTotal_EmptyCategories_Failure() throws Exception {
        // given
        CheapestBrandRequest request = new CheapestBrandRequest(List.of());
        String content = objectMapper.writeValueAsString(request);

        // when & then
        mockMvc.perform(post("/products/cheapest-brand")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("카테고리를 선택해주세요.")));
    }

    @Test
    void findCheapestBrandTotal_NoProductsInCategory_Failure() throws Exception {
        // given
        List<Category> categories = Arrays.asList(Category.HAT, Category.SOCKS);
        CheapestBrandRequest request = new CheapestBrandRequest(categories);
        String content = objectMapper.writeValueAsString(request);

        // when & then
        mockMvc.perform(post("/products/cheapest-brand")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("선택한 카테고리의 상품이 없습니다.")));
    }
} 