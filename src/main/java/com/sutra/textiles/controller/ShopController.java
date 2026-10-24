package com.sutra.textiles.controller;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import com.sutra.textiles.entity.Product;
import com.sutra.textiles.repository.ProductRepository;

@Controller
public class ShopController {

    private final ProductRepository productRepository;

    private static final Set<String> CATEGORIES = Set.of(
            "sarees", "lehengas", "salwar-suits", "regional"
    );

    private static final Set<String> REGIONS = Set.of(
            "rajasthan", "bengal", "south-india", "punjab",
            "gujarat", "kashmir", "maharashtra"
    );

    private static final Map<String, String> CATEGORY_LABELS = Map.of(
            "sarees", "Sarees",
            "lehengas", "Lehengas",
            "salwar-suits", "Salwar Suits",
            "regional", "Regional Wear"
    );

    private static final Map<String, String> REGION_LABELS = Map.of(
            "rajasthan", "Rajasthani",
            "bengal", "Bengali",
            "south-india", "South Indian",
            "punjab", "Punjabi",
            "gujarat", "Gujarati",
            "kashmir", "Kashmiri",
            "maharashtra", "Maharashtrian"
    );

    public ShopController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping("/shop")
    public String shop(
            @RequestParam(required = false) String cat,
            @RequestParam(required = false) String region,
            Model model) {

        if (cat != null && !CATEGORIES.contains(cat)) {
            cat = null;
        }

        if (region != null && !REGIONS.contains(region)) {
            region = null;
        }

        model.addAttribute("initialCategory", cat == null ? "" : cat);
        model.addAttribute("initialRegion", region == null ? "" : region);

        return "shop";
    }

    @GetMapping("/api/products")
    @ResponseBody
    public Page<Product> getProducts(
            @RequestParam(required = false) String cat,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "newest") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size) {

        if (cat != null && !cat.isBlank() && !CATEGORIES.contains(cat)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Invalid product category");
        }

        if (region != null && !region.isBlank()
                && !REGIONS.contains(region)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Invalid product region");
        }

        if (minPrice != null && minPrice.signum() < 0
                || maxPrice != null && maxPrice.signum() < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Price cannot be negative");
        }

        if (minPrice != null && maxPrice != null
                && minPrice.compareTo(maxPrice) > 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Invalid price range");
        }

        if (!Set.of("newest", "price-asc", "price-desc").contains(sort)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Invalid sort option");
        }

        if (page < 0 || size < 1 || size > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Invalid pagination values");
        }

        Sort sorting = switch (sort) {
            case "price-asc" -> Sort.by(Sort.Direction.ASC, "price")
                    .and(Sort.by(Sort.Direction.DESC, "id"));
            case "price-desc" -> Sort.by(Sort.Direction.DESC, "price")
                    .and(Sort.by(Sort.Direction.DESC, "id"));
            default -> Sort.by(Sort.Direction.DESC, "createdAt")
                    .and(Sort.by(Sort.Direction.DESC, "id"));
        };

        Pageable pageable = PageRequest.of(page, size, sorting);

        return productRepository.findShopProducts(
                blankToNull(cat),
                blankToNull(region),
                minPrice,
                maxPrice,
                pageable
        );
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }
}
