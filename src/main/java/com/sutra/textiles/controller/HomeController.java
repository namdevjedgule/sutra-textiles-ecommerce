package com.sutra.textiles.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping({"/", "/home"})
    public String home(Model model) {

        List<FeaturedProduct> featuredProducts = List.of(
            new FeaturedProduct(
                "Bandhani Silk Saree",
                "Rajasthan",
                new BigDecimal("3450.00"),
                "Bestseller",
                "tint-1"
            ),
            new FeaturedProduct(
                "Chanderi Anarkali Suit",
                "Madhya Pradesh",
                new BigDecimal("2790.00"),
                "",
                "tint-3"
            ),
            new FeaturedProduct(
                "Kanjeevaram Bridal Lehenga",
                "Tamil Nadu",
                new BigDecimal("8990.00"),
                "New",
                "tint-2"
            ),
            new FeaturedProduct(
                "Phulkari Cotton Kurti",
                "Punjab",
                new BigDecimal("1650.00"),
                "",
                "tint-4"
            )
        );

        model.addAttribute("featuredProducts", featuredProducts);

        return "index";
    }

    public record FeaturedProduct(
        String name,
        String region,
        BigDecimal price,
        String tag,
        String tintClass
    ) {}
}
