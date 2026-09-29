package vn.iotstar.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationProvider;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import vn.iotstar.filter.JwtAuthenticationFilter;

@Configuration
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final AuthenticationProvider authenticationProvider;

    public SecurityConfiguration(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            AuthenticationProvider authenticationProvider) {

        this.jwtAuthenticationFilter =
            jwtAuthenticationFilter;

        this.authenticationProvider =
            authenticationProvider;
    }

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http)
            throws Exception {

        http

            .csrf(
                csrf ->
                    csrf.disable()
            )

            .authorizeHttpRequests(
                auth -> auth

                    // REST API đăng nhập/đăng ký
                    .requestMatchers(
                        "/auth/**"
                    )
                    .permitAll()

                    // Giao diện
                    .requestMatchers(
                        "/",
                        "/login",
                        "/login.html",
                        "/register.html",
                        "/profile.html"
                    )
                    .permitAll()

                    // static resources
                    .requestMatchers(
                        "/js/**",
                        "/css/**",
                        "/images/**",
                        "/favicon.ico"
                    )
                    .permitAll()

                    // Các API còn lại cần JWT
                    .anyRequest()
                    .authenticated()
            )

            .sessionManagement(
                session ->
                    session
                        .sessionCreationPolicy(
                            SessionCreationPolicy.STATELESS
                        )
            )

            .authenticationProvider(
                authenticationProvider
            )

            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
}