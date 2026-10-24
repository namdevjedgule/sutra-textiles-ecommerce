
package com.sutra.textiles.controller;

import com.sutra.textiles.dto.CheckoutRequest;
import com.sutra.textiles.service.CheckoutService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {

    private final CheckoutService checkoutService;

    public CheckoutController(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    /**
     * Display the checkout page.
     */
    @GetMapping
    public String showCheckout(
            Principal principal,
            Model model,
            RedirectAttributes redirectAttributes) {

        String username = requireUsername(principal);

        var summary = checkoutService.getCheckoutSummary(username);

        if (summary == null || summary.isEmpty()) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Your cart is empty. Please add a product first."
            );

            return "redirect:/cart";
        }

        model.addAttribute("checkoutRequest", new CheckoutRequest());
        model.addAttribute("checkoutSummary", summary);

        return "checkout/checkout";
    }

    /**
     * Validate shipping details and place the order.
     */
    @PostMapping("/place-order")
    public String placeOrder(
            @Valid @ModelAttribute("checkoutRequest")
            CheckoutRequest checkoutRequest,
            BindingResult bindingResult,
            Principal principal,
            Model model,
            RedirectAttributes redirectAttributes) {

        String username = requireUsername(principal);

        if (bindingResult.hasErrors()) {
            model.addAttribute(
                    "checkoutSummary",
                    checkoutService.getCheckoutSummary(username)
            );

            return "checkout/checkout";
        }

        try {
            /*
             * The service must calculate prices from the database,
             * validate stock, and save the order transactionally.
             */
            Long orderId = checkoutService.placeOrder(
                    username,
                    checkoutRequest
            );

            return "redirect:/checkout/confirmation/" + orderId;

        } catch (IllegalArgumentException ex) {
            model.addAttribute("checkoutError", ex.getMessage());
            model.addAttribute(
                    "checkoutSummary",
                    checkoutService.getCheckoutSummary(username)
            );

            return "checkout/checkout";
        }
    }

    /**
     * Show confirmation only for the customer's own order.
     */
    @GetMapping("/confirmation/{orderId}")
    public String showConfirmation(
            @PathVariable Long orderId,
            Principal principal,
            Model model) {

        String username = requireUsername(principal);

        model.addAttribute(
                "order",
                checkoutService.getOrderForUser(username, orderId)
        );

        return "checkout/confirmation";
    }

    private String requireUsername(Principal principal) {
        if (principal == null) {
            throw new org.springframework.security.authentication
                    .AuthenticationCredentialsNotFoundException(
                    "Please log in to continue to checkout."
            );
        }

        return principal.getName();
    }
}
