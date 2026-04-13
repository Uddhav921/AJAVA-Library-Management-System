package com.image.ajlibrary.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Disables CSRF and opens all endpoints without authentication.
     *
     * NOTE: For production, replace this with JWT-based authentication:
     * 1. Add jjwt dependency
     * 2. Create JwtUtil, JwtFilter, and UserDetailsService implementation
     * 3. Restrict admin endpoints to ADMIN role
     * 4. Encode passwords with BCryptPasswordEncoder
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }
}
