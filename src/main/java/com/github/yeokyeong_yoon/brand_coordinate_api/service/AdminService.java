package com.github.yeokyeong_yoon.brand_coordinate_api.service;

import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Brand;
import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Product;
import com.github.yeokyeong_yoon.brand_coordinate_api.dto.BrandRequest;
import com.github.yeokyeong_yoon.brand_coordinate_api.dto.ProductRequest;
import com.github.yeokyeong_yoon.brand_coordinate_api.repository.BrandRepository;
import com.github.yeokyeong_yoon.brand_coordinate_api.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 운영자를 위한 브랜드와 상품 관리 서비스입니다.
 */
@Service
@RequiredArgsConstructor
public class AdminService {
    private final BrandRepository brandRepository;
    private final ProductRepository productRepository;

    /**
     * 새로운 브랜드를 등록합니다.
     */
    @Transactional
    public Brand registerBrand(BrandRequest request) {
        if (brandRepository.existsByName(request.name())) {
            throw new IllegalArgumentException("Brand with name " + request.name() + " already exists");
        }
        
        Brand brand = new Brand();
        brand.setName(request.name());
        return brandRepository.save(brand);
    }

    /**
     * 브랜드 정보를 수정합니다.
     */
    @Transactional
    public Brand updateBrand(Long brandId, BrandRequest request) {
        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new IllegalArgumentException("Brand not found with id: " + brandId));
        
        if (!brand.getName().equals(request.name()) && brandRepository.existsByName(request.name())) {
            throw new IllegalArgumentException("Brand with name " + request.name() + " already exists");
        }
        
        brand.setName(request.name());
        return brandRepository.save(brand);
    }

    /**
     * 브랜드를 삭제합니다.
     */
    @Transactional
    public void deleteBrand(Long brandId) {
        if (!brandRepository.existsById(brandId)) {
            throw new IllegalArgumentException("Brand not found with id: " + brandId);
        }
        brandRepository.deleteById(brandId);
    }

    /**
     * 새로운 상품을 등록합니다.
     */
    @Transactional
    public Product registerProduct(ProductRequest request) {
        Brand brand = brandRepository.findByName(request.brandName())
                .orElseThrow(() -> new IllegalArgumentException("Brand not found with name: " + request.brandName()));
        
        Product product = new Product();
        product.setBrand(brand);
        product.setCategory(request.category());
        product.setPrice(request.price());
        return productRepository.save(product);
    }

    /**
     * 상품 정보를 수정합니다.
     */
    @Transactional
    public Product updateProduct(Long productId, ProductRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + productId));
        
        Brand brand = brandRepository.findByName(request.brandName())
                .orElseThrow(() -> new IllegalArgumentException("Brand not found with name: " + request.brandName()));
        
        product.setBrand(brand);
        product.setCategory(request.category());
        product.setPrice(request.price());
        return productRepository.save(product);
    }

    /**
     * 상품을 삭제합니다.
     */
    @Transactional
    public void deleteProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new IllegalArgumentException("Product not found with id: " + productId);
        }
        productRepository.deleteById(productId);
    }

    /**
     * 모든 브랜드 목록을 조회합니다.
     */
    @Transactional(readOnly = true)
    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }

    /**
     * 모든 상품 목록을 조회합니다.
     */
    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
} 