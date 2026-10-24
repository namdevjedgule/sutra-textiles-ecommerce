
package com.sutra.textiles.dto;

import java.math.BigDecimal;

import com.sutra.textiles.entity.Cart;
import com.sutra.textiles.entity.CartItem;

public class CheckoutSummary {

    private final Cart cart;
    private final BigDecimal subtotal;
    private final BigDecimal shippingCharge;
    private final BigDecimal totalAmount;

    public CheckoutSummary(
            Cart cart,
            BigDecimal subtotal,
            BigDecimal shippingCharge,
            BigDecimal totalAmount) {

        this.cart = cart;
        this.subtotal = subtotal;
        this.shippingCharge = shippingCharge;
        this.totalAmount = totalAmount;
    }

    public Cart getCart() {
        return cart;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public BigDecimal getShippingCharge() {
        return shippingCharge;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public boolean isEmpty() {
        return cart == null
                || cart.getItems() == null
                || cart.getItems().isEmpty();
    }

    public BigDecimal getItemTotal(CartItem item) {
        if (item == null
                || item.getProduct() == null
                || item.getProduct().getPrice() == null
                || item.getQuantity() == null) {
            return BigDecimal.ZERO;
        }

        return item.getProduct().getPrice()
                .multiply(BigDecimal.valueOf(item.getQuantity()));
    }
}
