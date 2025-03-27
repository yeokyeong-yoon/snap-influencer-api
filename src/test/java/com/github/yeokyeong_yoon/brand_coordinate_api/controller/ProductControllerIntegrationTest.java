package com.github.yeokyeong_yoon.brand_coordinate_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.yeokyeong_yoon.brand_coordinate_api.dto.ProductRequest;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerIntegrationTest {

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

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        brandRepository.deleteAll();

        nike = brandRepository.save(new Brand("Nike"));
        adidas = brandRepository.save(new Brand("Adidas"));
    }

    @Test
    void registerProduct_Success() throws Exception {
        // given
        ProductRequest request = new ProductRequest("Nike", Category.TOP, 50000);
        String content = objectMapper.writeValueAsString(request);

        // when & then
        mockMvc.perform(post("/admin/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.brand", is("Nike")))
                .andExpect(jsonPath("$.data.category", is("TOP")))
                .andExpect(jsonPath("$.data.price", is(50000)));
    }

    @Test
    void registerProduct_NonExistentBrand_Failure() throws Exception {
        // given
        ProductRequest request = new ProductRequest("NonExistentBrand", Category.TOP, 50000);
        String content = objectMapper.writeValueAsString(request);

        // when & then
        mockMvc.perform(post("/admin/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("존재하지 않는 브랜드입니다.")));
    }

    @Test
    void getProducts_Success() throws Exception {
        // given
        productRepository.save(new Product(nike, Category.TOP, 50000));
        productRepository.save(new Product(adidas, Category.PANTS, 60000));

        // when & then
        mockMvc.perform(get("/admin/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].brand", is("Nike")))
                .andExpect(jsonPath("$.data[1].brand", is("Adidas")));
    }

    @Test
    void deleteProduct_Success() throws Exception {
        // given
        Product product = productRepository.save(new Product(nike, Category.TOP, 50000));

        // when & then
        mockMvc.perform(delete("/admin/products/" + product.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));
    }

    @Test
    void findLowestPricesByCategory_Success() throws Exception {
        // given
        productRepository.save(new Product(nike, Category.TOP, 50000));
        productRepository.save(new Product(adidas, Category.TOP, 45000));
        productRepository.save(new Product(nike, Category.PANTS, 60000));
        productRepository.save(new Product(adidas, Category.PANTS, 70000));

        // when & then
        mockMvc.perform(get("/products/lowest-prices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.TOP.brand", is("Adidas")))
                .andExpect(jsonPath("$.data.TOP.price", is(45000)))
                .andExpect(jsonPath("$.data.PANTS.brand", is("Nike")))
                .andExpect(jsonPath("$.data.PANTS.price", is(60000)));
    }

    @Test
    void findPriceRangeByCategory_Success() throws Exception {
        // given
        productRepository.save(new Product(nike, Category.TOP, 50000));
        productRepository.save(new Product(adidas, Category.TOP, 45000));

        // when & then
        mockMvc.perform(get("/products/price-range/TOP"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.minPrice", is(45000)))
                .andExpect(jsonPath("$.data.maxPrice", is(50000)));
    }
} 