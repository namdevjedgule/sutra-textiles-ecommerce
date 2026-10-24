
package com.sutra.textiles.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckoutRequest {

    @NotBlank(message = "Full name is required.")
    @Size(max = 150)
    private String fullName;

    @NotBlank(message = "Email is required.")
    @Email(message = "Enter a valid email address.")
    @Size(max = 150)
    private String email;

    @NotBlank(message = "Phone number is required.")
    @Pattern(
        regexp = "^[0-9+() -]{7,20}$",
        message = "Enter a valid phone number."
    )
    private String phone;

    @NotBlank(message = "Address is required.")
    @Size(max = 500)
    private String address;
    
    @Size(max = 200)
    private String addressLine2;

    @NotBlank(message = "City is required.")
    @Size(max = 100)
    private String city;

    @NotBlank(message = "State is required.")
    @Size(max = 100)
    private String state;

    @NotBlank(message = "Postal code is required.")
    @Size(max = 20)
    @Pattern(regexp = "^[A-Za-z0-9 -]{3,10}$")
    private String postalCode;

    @NotBlank(message = "Select a payment method.")
    private String paymentMethod;

}
