
package com.sutra.textiles.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
            .authorizeHttpRequests(auth -> auth

                // Public pages
                .requestMatchers(
                    "/",
                    "/home",
                    "/shop",
                    "/shop/**",
                    "/products/**",
                    "/about",
                    "/contact",
                    "/legacy",
                    "/our-legacy",
                    "/faq",
                    "/testimonial",
                    "/testimonials",
                    "/policy",
                    "/shipping-policy",
                    "/returns-policy",
                    "/privacy-policy",
                    "/terms",
                    "/search",
                    "/login",
                    "/register",
                    "/api/products/**"
                ).permitAll()
                
                .requestMatchers(
                	    "/policy.html",
                	    "/privacy-policy",
                	    "/privacy-policy.html",
                	    "/terms",
                	    "/terms.html",
                	    "/shipping-policy",
                	    "/shipping-policy.html",
                	    "/returns-policy",
                	    "/returns-policy.html"
                	).permitAll()

                // Public assets
                .requestMatchers(
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/image/**",
                    "/favicon.ico",
                    "/favicon_io/**"
                ).permitAll()

                // Login required
                .requestMatchers(
                    "/cart",
                    "/cart/**",
                    "/checkout",
                    "/checkout/**",
                    "/orders/**",
                    "/account/**",
                    "/wishlist",
                    "/wishlist/**"
                ).authenticated()

                .anyRequest().authenticated()
            )

            .formLogin(form -> form
            	    .loginPage("/login")
            	    .loginProcessingUrl("/login")
            	    .usernameParameter("email")
            	    .passwordParameter("password")
            	    .defaultSuccessUrl("/", false)
            	    .failureUrl("/login?error")
            	    .permitAll()
            	)

            .logout(logout -> logout
                .logoutSuccessUrl("/")
                .permitAll()
            );

        return http.build();
    }
}
