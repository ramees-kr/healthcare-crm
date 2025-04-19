package com.medicare.healthcarecrm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean // Replace the existing filterChain method with this one
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        // Allow access to static resources and the home/login page for everyone
                        .requestMatchers("/", "/index", "/login", "/css/**", "/js/**", "/images/**").permitAll()
                        // --- Authorization Rules ---
                        // Require ROLE_ADMIN for any URL starting with /admin/
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        // Require ROLE_EMPLOYEE for any URL starting with /employee/
                        .requestMatchers("/employee/**").hasRole("EMPLOYEE")
                        // Require authentication for the default success URL
                        .requestMatchers("/default").authenticated()
                        // --- Fallback Rule ---
                        // Any other request not matched above must be authenticated
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login") // Specify custom login page URL
                        .loginProcessingUrl("/login") // URL to submit username/password
                        .defaultSuccessUrl("/default", true) // Redirect handler after successful login
                        .permitAll() // Allow everyone to access the login page itself
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout")) // URL to trigger logout
                        .logoutSuccessUrl("/login?logout") // Redirect after logout
                        .invalidateHttpSession(true) // Invalidate session on logout
                        .deleteCookies("JSESSIONID") // Delete session cookie
                        .permitAll()
                )
                // Optional: Configure exception handling for access denied
                .exceptionHandling(exceptions -> exceptions
                        .accessDeniedPage("/access-denied") // Redirect to a custom access denied page if needed
                );


        return http.build();
    }

}