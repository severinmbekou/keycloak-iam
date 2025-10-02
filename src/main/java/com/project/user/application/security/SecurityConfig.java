package com.project.user.application.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
  private static final String[] FREE_SWARGER = {
    "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/v3/api-docs", "/webjars/**"
  };
  private final String[] FREE_POST_URL = {
    "/api/iam/users/register",
    "/api/iam/users/logout",
    "/api/iam/users/refresh-token",
    "/api/iam/users/login",
    "/api/iam/users/forgot-password",
    "/api/iam/token"
  };

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable);

    http.authorizeHttpRequests(
        auth ->
            auth.requestMatchers(FREE_SWARGER)
                .permitAll()
                .requestMatchers(HttpMethod.POST, FREE_POST_URL)
                .permitAll()
                .anyRequest()
                .authenticated());

    http.oauth2ResourceServer(
        resourceServer ->
            resourceServer.jwt(
                jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));

    return http.build();
  }

  private JwtAuthenticationConverter jwtAuthenticationConverter() {
    // You can customize authorities if needed here
    return new JwtAuthenticationConverter();
  }
}
