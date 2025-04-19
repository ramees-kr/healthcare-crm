package com.medicare.healthcarecrm.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collection;

@Controller
public class HomeController {

    @GetMapping("/")
    public String showHomePage() {
        // If user is already authenticated, redirect them to their default page
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            return "redirect:/default";
        }
        // Otherwise, show the login page (index.html will be repurposed or replaced by a login template)
        return "index"; // Or redirect to "/login" if you rename index.html
    }

    @GetMapping("/login")
    public String showLoginPage() {
        // Show the login page template (which should be index.html or a renamed version)
        return "index"; // Ensure your login form is in index.html
    }

    // Add a default success URL handler
    @GetMapping("/default")
    public String defaultAfterLogin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            if (authorities.stream().anyMatch(ga -> ga.getAuthority().equals("ROLE_ADMIN"))) {
                return "redirect:/admin"; // Redirect admin users
            } else if (authorities.stream().anyMatch(ga -> ga.getAuthority().equals("ROLE_EMPLOYEE"))) {
                return "redirect:/employee"; // Redirect employee users
            }
        }
        // Fallback or handle other roles if necessary
        return "redirect:/login?error";
    }

    // Add this mapping for the access denied page
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied"; // This is the name of the HTML template file
    }

}