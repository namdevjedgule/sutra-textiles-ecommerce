
package com.sutra.textiles.controller;

import com.sutra.textiles.service.CartService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/cart")
@Validated
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    /**
     * Display the current customer's cart.
     */
    @GetMapping
    public String viewCart(Principal principal, Model model) {
        String username = requireUsername(principal);

        model.addAttribute(
                "cart",
                cartService.getCartForUser(username)
        );

        return "cart";
    }

    /**
     * Add a product to the cart.
     */
    @PostMapping("/add")
    public String addToCart(
            @RequestParam @NotNull Long productId,
            @RequestParam(defaultValue = "1") @Min(1) int quantity,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        String username = requireUsername(principal);

        cartService.addItem(username, productId, quantity);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Product added to your cart."
        );

        return "redirect:/cart";
    }

    /**
     * Update the quantity of a cart item.
     */
    @PostMapping("/update")
    public String updateQuantity(
            @RequestParam @NotNull Long cartItemId,
            @RequestParam @Min(1) int quantity,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        String username = requireUsername(principal);

        cartService.updateQuantity(
                username,
                cartItemId,
                quantity
        );

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Cart updated successfully."
        );

        return "redirect:/cart";
    }

    /**
     * Remove an item from the cart.
     */
    @PostMapping("/remove/{cartItemId}")
    public String removeItem(
            @PathVariable Long cartItemId,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        String username = requireUsername(principal);

        cartService.removeItem(username, cartItemId);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Product removed from your cart."
        );

        return "redirect:/cart";
    }

    private String requireUsername(Principal principal) {
        if (principal == null) {
            throw new org.springframework.security.authentication
                    .AuthenticationCredentialsNotFoundException(
                    "Please log in to access your cart."
            );
        }

        return principal.getName();
    }
}

