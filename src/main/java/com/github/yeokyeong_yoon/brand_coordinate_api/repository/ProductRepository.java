package com.github.yeokyeong_yoon.brand_coordinate_api.repository;

import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Brand;
import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Category;
import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory(Category category);
    List<Product> findByBrand(Brand brand);
    List<Product> findByCategoryAndPriceIn(Category category, List<Integer> prices);
    Optional<Product> findFirstByCategoryOrderByPriceAsc(Category category);
    Optional<Product> findFirstByCategoryOrderByPriceDesc(Category category);
    boolean existsByBrandAndCategoryAndPrice(Brand brand, Category category, int price);
    List<Product> findByCategoryIn(List<Category> categories);

    default List<Product> findPriceRangeProductsByCategory(Category category) {
        List<Integer> priceRange = List.of(
            findFirstByCategoryOrderByPriceAsc(category).map(Product::getPrice).orElse(Integer.MAX_VALUE),
            findFirstByCategoryOrderByPriceDesc(category).map(Product::getPrice).orElse(Integer.MIN_VALUE)
        );
        return findByCategoryAndPriceIn(category, priceRange);
    }

    // Get all products in a category sorted by price
    List<Product> findByCategoryOrderByPriceAsc(Category category);
    List<Product> findByCategoryAndPriceOrderByPriceAsc(Category category, int price);
    List<Product> findByCategoryAndPriceOrderByPriceDesc(Category category, int price);
}