package com.github.yeokyeong_yoon.brand_coordinate_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.yeokyeong_yoon.brand_coordinate_api.dto.BrandRequest;
import com.github.yeokyeong_yoon.brand_coordinate_api.entity.Brand;
import com.github.yeokyeong_yoon.brand_coordinate_api.repository.BrandRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;

@SpringBootTest
@AutoConfigureMockMvc
public class BrandControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BrandRepository brandRepository;

    @BeforeEach
    void setUp() {
        brandRepository.deleteAll();
    }

    @Test
    void registerBrand_Success() throws Exception {
        // given
        BrandRequest request = new BrandRequest("Nike");
        String content = objectMapper.writeValueAsString(request);

        // when & then
        mockMvc.perform(post("/admin/brands")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.name", is("Nike")));
    }

    @Test
    void registerBrand_DuplicateName_Failure() throws Exception {
        // given
        Brand existingBrand = new Brand("Nike");
        brandRepository.save(existingBrand);

        BrandRequest request = new BrandRequest("Nike");
        String content = objectMapper.writeValueAsString(request);

        // when & then
        mockMvc.perform(post("/admin/brands")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("이미 존재하는 브랜드입니다.")));
    }
} 