package com.sutra.textiles.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PageController {

    @GetMapping("/about")
    public String about() {
        return "about";
    }

    @GetMapping("/contact")
    public String contact() {
        return "contact";
    }

    @GetMapping({"/legacy", "/our-legacy"})
    public String legacy() {
        return "legacy";
    }

    @GetMapping("/faq")
    public String faq() {
        return "faq";
    }

    @GetMapping({"/testimonials", "/testimonial"})
    public String testimonials() {
        return "testimonials";
    }
    
    @GetMapping({
    	"/policy",
        "/policy.html",
        "/privacy-policy",
        "/privacy-policy.html",
        "/terms",
        "/terms.html",
        "/shipping-policy",
        "/shipping-policy.html",
        "/returns-policy",
        "/returns-policy.html"
    })
    public String policyPage() {
        return "policy";
    }

    @GetMapping("/search")
    public String search(
            @RequestParam(required = false) String q,
            Model model) {

        model.addAttribute("searchQuery", q == null ? "" : q);

        return "search";
    }

    @GetMapping("/account")
    public String account() {
        return "account";
    }

    @GetMapping("/wishlist")
    public String wishlist() {
        return "wishlist";
    }
}

