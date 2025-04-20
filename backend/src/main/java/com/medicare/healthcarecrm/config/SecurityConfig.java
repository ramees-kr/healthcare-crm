package com.medicare.healthcarecrm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String[] SWAGGER_PATHS = {
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html"
    };

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // --- CSRF Configuration ---
                .csrf(csrf -> csrf
                                // Disable CSRF specifically for API paths
                                .ignoringRequestMatchers("/api/**")
                        // Keep CSRF enabled for all other paths (default)
                )
                .authorizeHttpRequests(authorize -> authorize
                        // --- Public Access ---
                        .requestMatchers("/", "/index", "/login", "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers(SWAGGER_PATHS).permitAll()

                        // --- MVC Authorization Rules ---
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/employee/**").hasRole("EMPLOYEE")
                        .requestMatchers("/default").authenticated()

                        // --- API Authorization Rules ---
                        .requestMatchers(HttpMethod.GET, "/api/customers/**", "/api/employees/**", "/api/tasks/**").hasAnyRole("ADMIN", "EMPLOYEE")
                        .requestMatchers("/api/customers/**", "/api/employees/**", "/api/tasks/**").hasRole("ADMIN")

                        // --- Fallback Rule ---
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/default", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .exceptionHandling(exceptions -> exceptions
                        .accessDeniedPage("/access-denied")
                )
        ;

        return http.build();
    }

}