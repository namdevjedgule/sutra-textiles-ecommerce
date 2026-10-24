
package com.sutra.textiles.service;

import com.sutra.textiles.dto.RegisterRequest;
import com.sutra.textiles.entity.Role;
import com.sutra.textiles.entity.User;
import com.sutra.textiles.repository.RoleRepository;
import com.sutra.textiles.repository.UserRepository;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistrationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void register(RegisterRequest request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Registration details are required.");
        }

        String fullName = request.getFullName() == null
                ? ""
                : request.getFullName().trim();

        String email = request.getEmail() == null
                ? ""
                : request.getEmail().trim().toLowerCase(
                        java.util.Locale.ROOT);

        String password = request.getPassword();
        String confirmPassword = request.getConfirmPassword();

        if (fullName.isBlank() || fullName.length() > 100) {
            throw new IllegalArgumentException(
                    "Enter a name of up to 100 characters.");
        }

        if (email.isBlank() || email.length() > 150
                || !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new IllegalArgumentException(
                    "Enter a valid email address.");
        }

        if (password == null || password.length() < 8
                || password.length() > 72) {
            throw new IllegalArgumentException(
                    "Password must contain between 8 and 72 characters.");
        }

        if (confirmPassword == null
                || !password.equals(confirmPassword)) {
            throw new IllegalArgumentException(
                    "Passwords do not match.");
        }

        if (userRepository.findByEmailIgnoreCase(email).isPresent()) {
            throw new IllegalArgumentException(
                    "An account with this email already exists.");
        }

        String phone = request.getPhone();

        if (phone != null && !phone.isBlank()) {
            phone = phone.trim();

            if (phone.length() > 20) {
                throw new IllegalArgumentException(
                        "Phone number must not exceed 20 characters.");
            }
        } else {
            phone = null;
        }

        Role customerRole = roleRepository
                .findByRoleNameAndIsActiveTrue("CUSTOMER")
                .orElseThrow(() ->
                        new IllegalStateException(
                                "CUSTOMER role is missing or inactive."));

        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setPhone(phone);
        user.setRole(customerRole);
        user.setEnabled(true);
        user.setIsActive(true);

        try {
            userRepository.saveAndFlush(user);
        } catch (DataIntegrityViolationException ex) {
            // Handles a concurrent registration with the same email.
            throw new IllegalArgumentException(
                    "An account with this email already exists.");
        }
    }
}
