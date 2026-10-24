
package com.sutra.textiles.service;

import com.sutra.textiles.dto.CheckoutRequest;
import com.sutra.textiles.dto.CheckoutSummary;
import com.sutra.textiles.entity.Cart;
import com.sutra.textiles.entity.CartItem;
import com.sutra.textiles.entity.Order;
import com.sutra.textiles.entity.OrderItem;
import com.sutra.textiles.Enum.OrderStatus;
import com.sutra.textiles.entity.Payment;
import com.sutra.textiles.Enum.PaymentMethod;
import com.sutra.textiles.Enum.PaymentStatus;
import com.sutra.textiles.entity.Product;
import com.sutra.textiles.entity.User;
import com.sutra.textiles.repository.CartRepository;
import com.sutra.textiles.repository.OrderRepository;
import com.sutra.textiles.repository.PaymentRepository;
import com.sutra.textiles.repository.UserRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;

@Service
public class CheckoutService {

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final BigDecimal shippingCharge;

    public CheckoutService(
            CartRepository cartRepository,
            OrderRepository orderRepository,
            PaymentRepository paymentRepository,
            UserRepository userRepository,
            @Value("${sutra.checkout.shipping-charge:0.00}")
            BigDecimal shippingCharge) {

        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
        this.shippingCharge = shippingCharge;

        if (shippingCharge.signum() < 0) {
            throw new IllegalArgumentException(
                    "Shipping charge cannot be negative.");
        }
    }

    @Transactional(readOnly = true)
    public CheckoutSummary getCheckoutSummary(String username) {
        Cart cart = getCartForUser(username);

        BigDecimal subtotal = calculateSubtotal(cart);

        BigDecimal actualShipping = cart.getItems().isEmpty()
                ? BigDecimal.ZERO
                : shippingCharge;

        return new CheckoutSummary(
                cart,
                subtotal,
                actualShipping,
                subtotal.add(actualShipping)
        );
    }

    @Transactional
    public Long placeOrder(
            String username,
            CheckoutRequest request) {

        String email = normalizeUsername(username);

        if (request == null) {
            throw new IllegalArgumentException(
                    "Checkout details are required.");
        }

        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User account not found."));

        Cart cart = getCartForUser(email);

        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException(
                    "Your cart is empty.");
        }

        validateCheckoutRequest(request);

        PaymentMethod paymentMethod = parsePaymentMethod(
                request.getPaymentMethod());

        // Recheck prices, availability and quantities at checkout.
        BigDecimal subtotal = BigDecimal.ZERO;

        for (CartItem item : cart.getItems()) {
            Product product = item.getProduct();

            if (product == null
                    || !Boolean.TRUE.equals(product.getIsActive())) {
                throw new IllegalArgumentException(
                        "One or more products are unavailable.");
            }

            if (product.getPrice() == null
                    || product.getPrice().signum() < 0) {
                throw new IllegalArgumentException(
                        "A product has an invalid price.");
            }

            if (item.getQuantity() == null
                    || item.getQuantity() < 1
                    || item.getQuantity() > 100) {
                throw new IllegalArgumentException(
                        "A cart item has an invalid quantity.");
            }

            if (product.getStockQuantity() == null
                    || product.getStockQuantity() < item.getQuantity()) {
                throw new IllegalArgumentException(
                        "Insufficient stock for " + product.getName());
            }

            subtotal = subtotal.add(
                    product.getPrice().multiply(
                            BigDecimal.valueOf(item.getQuantity())));
        }

        BigDecimal actualShipping = shippingCharge;
        BigDecimal zero = BigDecimal.ZERO;

        Order order = new Order();

        order.setOrderNumber(generateOrderNumber());
        order.setUser(user);
        order.setOrderStatus(OrderStatus.PENDING);
        order.setSubtotal(subtotal);
        order.setDiscountAmount(zero);
        order.setShippingAmount(actualShipping);
        order.setTaxAmount(zero);
        order.setGrandTotal(subtotal.add(actualShipping));
        order.setCurrency("INR");

        // Snapshot of the delivery details.
        order.setShippingName(request.getFullName().trim());
        order.setShippingPhone(request.getPhone().trim());
        order.setShippingAddressLine1(request.getAddress().trim());
        order.setShippingAddressLine2(
                blankToNull(request.getAddressLine2()));
        order.setShippingCity(request.getCity().trim());
        order.setShippingState(request.getState().trim());
        order.setShippingPostalCode(request.getPostalCode().trim());
        order.setShippingCountry("India");

        // Copy cart lines into historical order lines.
        for (CartItem cartItem :
                new ArrayList<>(cart.getItems())) {

            Product product = cartItem.getProduct();

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setProductName(product.getName());
            orderItem.setProductSku(product.getSku());
            orderItem.setUnitPrice(product.getPrice());
            orderItem.setQuantity(cartItem.getQuantity());

            orderItem.setLineTotal(
                    product.getPrice().multiply(
                            BigDecimal.valueOf(cartItem.getQuantity())));

            orderItem.setImageUrl(product.getPrimaryImageUrl());

            order.getItems().add(orderItem);
        }

        Order savedOrder = orderRepository.save(order);

        // Every checkout initially creates a pending payment attempt.
        Payment payment = new Payment();
        payment.setOrder(savedOrder);
        payment.setPaymentMethod(paymentMethod);
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setAmount(savedOrder.getGrandTotal());
        payment.setCurrency("INR");

        paymentRepository.save(payment);

        // Clear only after order and payment records are saved.
        for (CartItem item : new ArrayList<>(cart.getItems())) {
            item.setCart(null);
        }

        cart.getItems().clear();
        cartRepository.save(cart);

        return savedOrder.getId();
    }

    @Transactional(readOnly = true)
    public Order getOrderForUser(
            String username,
            Long orderId) {

        String email = normalizeUsername(username);

        if (orderId == null) {
            throw new IllegalArgumentException(
                    "Order ID is required.");
        }

        return orderRepository
                .findByIdAndUser_EmailIgnoreCase(orderId, email)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Order not found."));
    }

    private Cart getCartForUser(String username) {
        String email = normalizeUsername(username);

        return cartRepository.findByUser_EmailIgnoreCase(email)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Cart not found. Please add a product first."));
    }

    private BigDecimal calculateSubtotal(Cart cart) {
        BigDecimal subtotal = BigDecimal.ZERO;

        for (CartItem item : cart.getItems()) {
            if (item.getProduct() == null
                    || item.getProduct().getPrice() == null
                    || item.getQuantity() == null
                    || item.getQuantity() < 1) {
                throw new IllegalArgumentException(
                        "Your cart contains an invalid item.");
            }

            subtotal = subtotal.add(
                    item.getProduct().getPrice().multiply(
                            BigDecimal.valueOf(item.getQuantity())));
        }

        return subtotal;
    }

    private void validateCheckoutRequest(CheckoutRequest request) {
        if (isBlank(request.getFullName())
                || isBlank(request.getEmail())
                || isBlank(request.getPhone())
                || isBlank(request.getAddress())
                || isBlank(request.getCity())
                || isBlank(request.getState())
                || isBlank(request.getPostalCode())
                || isBlank(request.getPaymentMethod())) {

            throw new IllegalArgumentException(
                    "Please complete all required checkout fields.");
        }

        if (request.getFullName().trim().length() > 100
                || request.getEmail().trim().length() > 150
                || request.getPhone().trim().length() > 20
                || request.getAddress().trim().length() > 200
                || (request.getAddressLine2() != null
                    && request.getAddressLine2().trim().length() > 200)
                || request.getCity().trim().length() > 100
                || request.getState().trim().length() > 100
                || request.getPostalCode().trim().length() > 10) {

            throw new IllegalArgumentException(
                    "One or more checkout fields exceed the allowed length.");
        }

        if (shippingCharge.signum() < 0) {
            throw new IllegalStateException(
                    "Shipping charge cannot be negative.");
        }
    }

    private PaymentMethod parsePaymentMethod(String value) {
        try {
            PaymentMethod method = PaymentMethod.valueOf(
                    value.trim().toUpperCase(Locale.ROOT));

            if (method != PaymentMethod.RAZORPAY
                    && method != PaymentMethod.COD) {
                throw new IllegalArgumentException(
                        "This payment method is not currently supported.");
            }

            return method;
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(
                    "Choose a supported payment method: Razorpay or COD.");
        }
    }

    private String generateOrderNumber() {
        return "STI-" + UUID.randomUUID()
                .toString().replace("-", "")
                .substring(0, 16).toUpperCase(Locale.ROOT);
    }

    private String normalizeUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException(
                    "A logged-in user is required.");
        }

        return username.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank()
                ? null
                : value.trim();
    }
}
