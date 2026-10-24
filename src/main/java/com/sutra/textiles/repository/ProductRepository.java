package com.sutra.textiles.repository;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sutra.textiles.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

	@Query("""
		    SELECT p FROM Product p
		    WHERE p.isActive = true
		      AND (:category IS NULL OR p.category = :category)
		      AND (:region IS NULL OR p.region = :region)
		      AND (:minPrice IS NULL OR p.price >= :minPrice)
		      AND (:maxPrice IS NULL OR p.price <= :maxPrice)
		    """)
		Page<Product> findShopProducts(
		        @Param("category") String category,
		        @Param("region") String region,
		        @Param("minPrice") BigDecimal minPrice,
		        @Param("maxPrice") BigDecimal maxPrice,
		        Pageable pageable
		);
}

