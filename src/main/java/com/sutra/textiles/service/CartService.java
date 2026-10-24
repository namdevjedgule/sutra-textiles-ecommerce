
package com.sutra.textiles.service;

import com.sutra.textiles.entity.Cart;
import com.sutra.textiles.entity.CartItem;
import com.sutra.textiles.entity.Product;
import com.sutra.textiles.entity.User;
import com.sutra.textiles.repository.CartRepository;
import com.sutra.textiles.repository.ProductRepository;
import com.sutra.textiles.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartService(
            CartRepository cartRepository,
            ProductRepository productRepository,
            UserRepository userRepository) {

        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Cart getCartForUser(String username) {
        return getOrCreateCart(username);
    }

    @Transactional
    public Cart addItem(
            String username,
            Long productId,
            int quantity) {

        validateQuantity(quantity);

        Cart cart = getOrCreateCart(username);

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Product not found."));

        ensureProductAvailable(product);

        CartItem existingItem = cart.getItems()
                .stream()
                .filter(item -> item.getProduct() != null
                        && Objects.equals(
                                item.getProduct().getId(), productId))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            int newQuantity = existingItem.getQuantity() + quantity;
            validateQuantity(newQuantity);
            validateStock(product, newQuantity);
            existingItem.setQuantity(newQuantity);
        } else {
            validateStock(product, quantity);

            CartItem item = new CartItem();
            item.setCart(cart);
            item.setProduct(product);
            item.setQuantity(quantity);

            cart.getItems().add(item);
        }

        return cartRepository.save(cart);
    }

    @Transactional
    public Cart updateQuantity(
            String username,
            Long cartItemId,
            int quantity) {

        validateQuantity(quantity);

        Cart cart = getOrCreateCart(username);
        CartItem item = findCartItem(cart, cartItemId);

        Product product = item.getProduct();
        ensureProductAvailable(product);
        validateStock(product, quantity);

        item.setQuantity(quantity);

        return cartRepository.save(cart);
    }

    @Transactional
    public Cart removeItem(
            String username,
            Long cartItemId) {

        Cart cart = getOrCreateCart(username);
        CartItem item = findCartItem(cart, cartItemId);

        cart.getItems().remove(item);
        item.setCart(null);

        return cartRepository.save(cart);
    }

    @Transactional
    public void clearCart(String username) {
        Cart cart = getOrCreateCart(username);

        for (CartItem item : cart.getItems()) {
            item.setCart(null);
        }

        cart.getItems().clear();
        cartRepository.save(cart);
    }

    private Cart getOrCreateCart(String username) {
        String email = normalizeUsername(username);

        return cartRepository.findByUser_EmailIgnoreCase(email)
                .orElseGet(() -> {
                    User user = userRepository
                            .findByEmailIgnoreCase(email)
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "User account not found."));

                    Cart cart = new Cart();
                    cart.setUser(user);

                    return cartRepository.save(cart);
                });
    }

    private CartItem findCartItem(Cart cart, Long cartItemId) {
        if (cartItemId == null) {
            throw new IllegalArgumentException(
                    "Cart item ID is required.");
        }

        return cart.getItems()
                .stream()
                .filter(item -> Objects.equals(
                        item.getId(), cartItemId))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Cart item not found."));
    }

    private void ensureProductAvailable(Product product) {
        if (product == null
                || !Boolean.TRUE.equals(product.getIsActive())) {
            throw new IllegalArgumentException(
                    "This product is currently unavailable.");
        }

        if (product.getPrice() == null
                || product.getPrice().signum() < 0) {
            throw new IllegalArgumentException(
                    "This product has an invalid price.");
        }
    }

    private void validateStock(Product product, int quantity) {
        if (product.getStockQuantity() == null
                || product.getStockQuantity() < quantity) {
            throw new IllegalArgumentException(
                    "Insufficient stock for " + product.getName() + ".");
        }
    }

    private void validateQuantity(int quantity) {
        if (quantity < 1 || quantity > 100) {
            throw new IllegalArgumentException(
                    "Quantity must be between 1 and 100.");
        }
    }

    private String normalizeUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException(
                    "A logged-in user is required.");
        }

        return username.trim();
    }
}
