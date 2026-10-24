package com.sutra.textiles.controller;

import com.sutra.textiles.dto.RegisterRequest;
import com.sutra.textiles.service.RegistrationService;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

	private final RegistrationService registrationService;

	public AuthController(RegistrationService registrationService) {
		this.registrationService = registrationService;
	}

	@GetMapping("/login")
	public String login(Model model) {
		if (!model.containsAttribute("registerRequest")) {
			model.addAttribute("registerRequest", new RegisterRequest());
		}
		return "account";
	}

	@GetMapping("/register")
	public String showRegistration(Model model) {
		if (!model.containsAttribute("registerRequest")) {
			model.addAttribute("registerRequest", new RegisterRequest());
		}
		return "account";
	}

	@PostMapping("/register")
	public String register(@Valid @ModelAttribute("registerRequest") RegisterRequest request,
			BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {

		if (!bindingResult.hasErrors() && !request.getPassword().equals(request.getConfirmPassword())) {
			bindingResult.rejectValue("confirmPassword", "password.mismatch", "Passwords do not match.");
		}

		if (bindingResult.hasErrors()) {
			return "account";
		}

		try {
			registrationService.register(request);
			redirectAttributes.addFlashAttribute("successMessage", "Account created successfully. Please sign in.");

			return "redirect:/login?registered";

		} catch (IllegalArgumentException ex) {
			model.addAttribute("registrationError", ex.getMessage());
			return "account";
		}
	}
}