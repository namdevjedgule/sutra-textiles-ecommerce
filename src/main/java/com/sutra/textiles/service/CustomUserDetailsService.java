
package com.sutra.textiles.service;

import com.sutra.textiles.entity.User;
import com.sutra.textiles.repository.UserRepository;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        if (email == null || email.isBlank()) {
            throw new UsernameNotFoundException(
                    "Email address is required.");
        }

        User user = userRepository
                .findByEmailIgnoreCase(email.trim())
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "Account not found."));

        if (user.getRole() == null
                || user.getRole().getRoleName() == null) {
            throw new UsernameNotFoundException(
                    "Account has no assigned role.");
        }

        String roleName = user.getRole().getRoleName();

        if (!roleName.equals("ADMIN")
                && !roleName.equals("CUSTOMER")) {
            throw new UsernameNotFoundException(
                    "Account has an invalid role.");
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .roles(roleName)
                .disabled(!user.isEnabled())
                .build();
    }
}
