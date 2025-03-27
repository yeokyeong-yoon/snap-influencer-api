package com.github.yeokyeong_yoon.brand_coordinate_api.service;

import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Brand;
import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Category;
import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Product;
import com.github.yeokyeong_yoon.brand_coordinate_api.dto.CheapestBrandResponse;
import com.github.yeokyeong_yoon.brand_coordinate_api.repository.BrandRepository;
import com.github.yeokyeong_yoon.brand_coordinate_api.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BrandService {

    private final BrandRepository brandRepository;
    private final ProductRepository productRepository;

    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }

    @Transactional
    public Brand registerBrand(String name) {
        if (brandRepository.existsByName(name)) {
            throw new IllegalArgumentException("이미 등록된 브랜드입니다: " + name);
        }
        Brand brand = new Brand();
        brand.setName(name);
        return brandRepository.save(brand);
    }

    public Brand getBrandByName(String name) {
        return brandRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("브랜드를 찾을 수 없습니다: " + name));
    }

    @Transactional
    public void deleteBrand(Long id) {
        if (!brandRepository.existsById(id)) {
            throw new IllegalArgumentException("브랜드를 찾을 수 없습니다: " + id);
        }
        brandRepository.deleteById(id);
    }

    public CheapestBrandResponse findCheapestBrandTotal(List<Category> categories) {
        if (categories == null || categories.isEmpty()) {
            throw new IllegalArgumentException("카테고리를 선택해주세요.");
        }

        // 1. 모든 상품 조회
        List<Product> allProducts = productRepository.findAll();
        if (allProducts.isEmpty()) {
            throw new IllegalArgumentException("등록된 상품이 없습니다.");
        }

        // 2. 브랜드별로 상품 그룹화
        Map<Brand, Map<Category, Integer>> brandPrices = new HashMap<>();
        for (Product product : allProducts) {
            brandPrices.computeIfAbsent(product.getBrand(), k -> new HashMap<>())
                    .put(product.getCategory(), product.getPrice());
        }

        // 3. 선택된 카테고리의 상품을 모두 가진 브랜드만 필터링하고 총액 계산
        List<CheapestBrandResponse.BrandTotal> brandTotals = new ArrayList<>();
        for (Map.Entry<Brand, Map<Category, Integer>> entry : brandPrices.entrySet()) {
            Brand brand = entry.getKey();
            Map<Category, Integer> prices = entry.getValue();

            // 브랜드가 모든 선택된 카테고리의 상품을 가지고 있는지 확인
            if (prices.keySet().containsAll(categories)) {
                // 카테고리별 가격 정보 생성
                List<CheapestBrandResponse.CategoryPrice> categoryPrices = new ArrayList<>();
                int total = 0;

                for (Category category : categories) {
                    int price = prices.get(category);
                    categoryPrices.add(new CheapestBrandResponse.CategoryPrice(
                            category.name(),
                            price
                    ));
                    total += price;
                }

                // 브랜드 정보 추가
                brandTotals.add(new CheapestBrandResponse.BrandTotal(
                        brand.getName(),
                        categoryPrices,
                        total
                ));
            }
        }

        if (brandTotals.isEmpty()) {
            return new CheapestBrandResponse(Collections.emptyList());
        }

        // 4. 총액으로 정렬
        brandTotals.sort(Comparator.comparingInt(CheapestBrandResponse.BrandTotal::total));

        // 5. 최저가 브랜드들만 선택 (동일한 가격이 있을 수 있음)
        int lowestTotal = brandTotals.get(0).total();
        List<CheapestBrandResponse.BrandTotal> cheapestBrands = brandTotals.stream()
                .filter(bt -> bt.total() == lowestTotal)
                .collect(Collectors.toList());

        return new CheapestBrandResponse(cheapestBrands);
    }
} 
