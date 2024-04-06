package com.chatty.config;

import com.chatty.handler.CustomAccessDeniedHandler;
import com.chatty.handler.JwtAuthenticationEntryPoint;
import com.chatty.jwt.JwtTokenFilter;
import com.chatty.jwt.JwtTokenProvider;
import com.chatty.service.user.UserDetailsServiceImpl;
import com.chatty.validator.TokenValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenValidator tokenValidator;
    private final UserDetailsServiceImpl userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        return httpSecurity
                .httpBasic(HttpBasicConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/reviews/home").hasRole("USER")
                        .requestMatchers("/reviews/**").authenticated()
                        .requestMatchers("/users/birth", "/users/gender").hasRole("ANONYMOUS")
                        .requestMatchers(HttpMethod.GET, "/v1/interests").permitAll()
                        .requestMatchers("/users/**").hasAnyRole("USER", "ANONYMOUS")
                        .anyRequest().hasAnyRole("USER", "ADMIN")
                )
                .addFilterBefore(new JwtTokenFilter(jwtTokenProvider, tokenValidator, userDetailsService), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(new JwtAuthenticationEntryPoint())
                        .accessDeniedHandler(new CustomAccessDeniedHandler(HttpStatus.FORBIDDEN)))
                .build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(
                "/users/join",
                "/users/login",
                "/check/**",
                "/ws",
//                "/auth/**",
                "/auth/mobile",
                "/auth/refresh",
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/index.html",
                "/matching/**",
                "/ws/**",
                "/signaling/**"
        );
    }
}