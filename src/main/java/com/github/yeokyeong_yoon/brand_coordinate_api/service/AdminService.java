package com.github.yeokyeong_yoon.brand_coordinate_api.service;

import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Brand;
import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Product;
import com.github.yeokyeong_yoon.brand_coordinate_api.dto.BrandRequest;
import com.github.yeokyeong_yoon.brand_coordinate_api.dto.ProductRequest;
import com.github.yeokyeong_yoon.brand_coordinate_api.repository.BrandRepository;
import com.github.yeokyeong_yoon.brand_coordinate_api.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 운영자를 위한 브랜드와 상품 관리 서비스입니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {
    private final BrandRepository brandRepository;
    private final ProductRepository productRepository;

    /**
     * 새로운 브랜드를 등록합니다.
     */
    @Transactional
    public Brand registerBrand(BrandRequest request) {
        log.info("Attempting to register brand with name: {}", request.name());
        if (brandRepository.existsByName(request.name())) {
            log.error("Brand with name {} already exists", request.name());
            throw new IllegalArgumentException("Brand with name " + request.name() + " already exists");
        }
        
        Brand brand = new Brand();
        brand.setName(request.name());
        Brand savedBrand = brandRepository.save(brand);
        log.info("Successfully registered brand: {}", savedBrand.getName());
        return savedBrand;
    }

    /**
     * 브랜드 정보를 수정합니다.
     */
    @Transactional
    public Brand updateBrand(Long brandId, BrandRequest request) {
        log.info("Attempting to update brand with id: {} to name: {}", brandId, request.name());
        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> {
                    log.error("Brand not found with id: {}", brandId);
                    return new IllegalArgumentException("Brand not found with id: " + brandId);
                });
        
        if (!brand.getName().equals(request.name()) && brandRepository.existsByName(request.name())) {
            log.error("Brand with name {} already exists", request.name());
            throw new IllegalArgumentException("Brand with name " + request.name() + " already exists");
        }
        
        brand.setName(request.name());
        Brand updatedBrand = brandRepository.save(brand);
        log.info("Successfully updated brand: {}", updatedBrand.getName());
        return updatedBrand;
    }

    /**
     * 브랜드를 삭제합니다.
     */
    @Transactional
    public void deleteBrand(Long brandId) {
        log.info("Attempting to delete brand with id: {}", brandId);
        if (!brandRepository.existsById(brandId)) {
            log.error("Brand not found with id: {}", brandId);
            throw new IllegalArgumentException("Brand not found with id: " + brandId);
        }
        brandRepository.deleteById(brandId);
        log.info("Successfully deleted brand with id: {}", brandId);
    }

    /**
     * 새로운 상품을 등록합니다.
     */
    @Transactional
    public Product registerProduct(ProductRequest request) {
        log.info("Attempting to register product for brand: {}, category: {}, price: {}", 
                request.brand(), request.category(), request.price());
        
        Brand brand = brandRepository.findByName(request.brand())
                .orElseThrow(() -> {
                    log.error("Brand not found with name: {}", request.brand());
                    return new IllegalArgumentException("Brand not found with name: " + request.brand());
                });
        
        Product product = new Product();
        product.setBrand(brand);
        product.setCategory(request.category());
        product.setPrice(request.price());
        
        Product savedProduct = productRepository.save(product);
        log.info("Successfully registered product with id: {} for brand: {}", 
                savedProduct.getId(), savedProduct.getBrand().getName());
        return savedProduct;
    }

    /**
     * 상품 정보를 수정합니다.
     */
    @Transactional
    public Product updateProduct(Long productId, ProductRequest request) {
        log.info("Attempting to update product with id: {} for brand: {}, category: {}, price: {}", 
                productId, request.brand(), request.category(), request.price());
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.error("Product not found with id: {}", productId);
                    return new IllegalArgumentException("Product not found with id: " + productId);
                });
        
        Brand brand = brandRepository.findByName(request.brand())
                .orElseThrow(() -> {
                    log.error("Brand not found with name: {}", request.brand());
                    return new IllegalArgumentException("Brand not found with name: " + request.brand());
                });
        
        product.setBrand(brand);
        product.setCategory(request.category());
        product.setPrice(request.price());
        
        Product updatedProduct = productRepository.save(product);
        log.info("Successfully updated product with id: {} for brand: {}", 
                updatedProduct.getId(), updatedProduct.getBrand().getName());
        return updatedProduct;
    }

    /**
     * 상품을 삭제합니다.
     */
    @Transactional
    public void deleteProduct(Long productId) {
        log.info("Attempting to delete product with id: {}", productId);
        if (!productRepository.existsById(productId)) {
            log.error("Product not found with id: {}", productId);
            throw new IllegalArgumentException("Product not found with id: " + productId);
        }
        productRepository.deleteById(productId);
        log.info("Successfully deleted product with id: {}", productId);
    }

    /**
     * 모든 브랜드 목록을 조회합니다.
     */
    @Transactional(readOnly = true)
    public List<Brand> getAllBrands() {
        log.debug("Fetching all brands");
        return brandRepository.findAll();
    }

    /**
     * 모든 상품 목록을 조회합니다.
     */
    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        log.debug("Fetching all products");
        return productRepository.findAll();
    }
} 