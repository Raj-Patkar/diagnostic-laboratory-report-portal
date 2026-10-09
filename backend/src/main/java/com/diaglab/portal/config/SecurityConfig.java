package com.diaglab.portal.config;

import com.diaglab.portal.service.PortalUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cors = new CorsConfiguration();
        cors.setAllowedOrigins(List.of("http://localhost:5173"));
        cors.setAllowedMethods(
                List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
        );
        cors.setAllowedHeaders(List.of("Content-Type", "Accept"));
        cors.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cors);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            SecurityContextRepository contextRepository,
            PortalUserDetailsService userDetailsService
    ) throws Exception {

        http
            .cors(Customizer.withDefaults())

            // For this local academic MVP, CSRF is disabled to simplify
            // REST API testing. Re-enable it before production deployment.
            .csrf(csrf -> csrf.disable())

            .securityContext(context -> context
                    .securityContextRepository(contextRepository)
            )

            .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                    .sessionFixation(fixation -> fixation.changeSessionId())
            )

            .userDetailsService(userDetailsService)

            .authorizeHttpRequests(auth -> auth
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    .requestMatchers("/api/health", "/error").permitAll()
                    .requestMatchers("/api/auth/login").permitAll()

                    .requestMatchers("/api/auth/logout").authenticated()

                    .requestMatchers(HttpMethod.GET, "/api/patients/**")
                        .hasAnyRole("ADMIN", "TECHNICIAN", "DOCTOR")
                    .requestMatchers(HttpMethod.POST, "/api/patients/**")
                        .hasAnyRole("ADMIN", "TECHNICIAN")
                    .requestMatchers(HttpMethod.PUT, "/api/patients/**")
                        .hasAnyRole("ADMIN", "TECHNICIAN")

                    .requestMatchers(HttpMethod.GET, "/api/tests/**")
                        .hasAnyRole("ADMIN", "TECHNICIAN", "DOCTOR")
                    .requestMatchers(HttpMethod.POST, "/api/tests/**")
                        .hasAnyRole("ADMIN", "TECHNICIAN")
                    .requestMatchers(HttpMethod.PUT, "/api/tests/**")
                        .hasAnyRole("ADMIN", "TECHNICIAN")

                    .requestMatchers(HttpMethod.GET, "/api/reports/**")
                        .hasAnyRole("ADMIN", "TECHNICIAN", "DOCTOR")
                    .requestMatchers(HttpMethod.POST, "/api/reports/**")
                        .hasAnyRole("ADMIN", "TECHNICIAN")
                    .requestMatchers(HttpMethod.PUT, "/api/reports/**")
                        .hasAnyRole("ADMIN", "TECHNICIAN")

                    .requestMatchers("/api/dashboard/**")
                        .hasAnyRole("ADMIN", "TECHNICIAN", "DOCTOR")
                    .requestMatchers("/api/activity/**")
                        .hasRole("ADMIN")

                    .anyRequest().authenticated()
            );

        return http.build();
    }
}