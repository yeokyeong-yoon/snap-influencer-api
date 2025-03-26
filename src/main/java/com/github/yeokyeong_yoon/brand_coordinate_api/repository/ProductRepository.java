package com.github.yeokyeong_yoon.brand_coordinate_api.repository;

import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Brand;
import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Category;
import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory(Category category);
    List<Product> findByBrand(Brand brand);

    @Query(value = """
            WITH RankedProducts AS (
                SELECT p.*,
                    RANK() OVER (PARTITION BY p.category ORDER BY p.price) as price_rank,
                    RANK() OVER (PARTITION BY p.category ORDER BY p.price DESC) as price_rank_desc
                FROM product p
                WHERE p.category = :category
            )
            SELECT * FROM RankedProducts
            WHERE price_rank = 1 OR price_rank_desc = 1
            """, nativeQuery = true)
    List<Product> findPriceRangeProductsByCategory(@Param("category") String category);

    // Derived query methods for simpler queries
    Optional<Product> findFirstByCategoryOrderByPriceAsc(Category category);
    Optional<Product> findFirstByCategoryOrderByPriceDesc(Category category);
    
    // For getting all products in a category sorted by price
    List<Product> findByCategoryOrderByPriceAsc(Category category);

    @Query("SELECT p FROM Product p JOIN p.brand b WHERE p.category = :category AND p.price = (SELECT MIN(p2.price) FROM Product p2 WHERE p2.category = :category)")
    List<Product> findCheapestProductsByCategory(@Param("category") Category category);

    @Query("SELECT p FROM Product p JOIN p.brand b WHERE p.category = :category AND p.price = (SELECT MAX(p2.price) FROM Product p2 WHERE p2.category = :category)")
    List<Product> findMostExpensiveProductsByCategory(@Param("category") Category category);
} 