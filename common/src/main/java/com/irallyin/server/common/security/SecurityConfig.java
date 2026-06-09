package com.irallyin.server.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RequestSignatureFilter requestSignatureFilter;
    private final ObjectMapper objectMapper;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authException) -> {
                            log.warn("Security authentication rejected: method={} path={} authorizationPresent={} reason={}",
                                    request.getMethod(),
                                    request.getRequestURI(),
                                    request.getHeader("Authorization") != null,
                                    authException.getMessage());
                            writeJsonError(response, HttpServletResponse.SC_UNAUTHORIZED,
                                    10002, "未认证或Token已过期");
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            log.warn("Security access denied: method={} path={} authorizationPresent={} reason={}",
                                    request.getMethod(),
                                    request.getRequestURI(),
                                    request.getHeader("Authorization") != null,
                                    accessDeniedException.getMessage());
                            writeJsonError(response, HttpServletResponse.SC_FORBIDDEN,
                                    10003, "无权限访问");
                        })
                )
                .authorizeHttpRequests(auth -> auth
                        // 公开端点
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/actuator/**"
                        ).permitAll()
                        // PC 后台静态页面与后台 API。后台 API 由 web 模块 AdminAuthenticationFilter 校验。
                        .requestMatchers(
                                "/admin/**",
                                "/api/admin/**"
                        ).permitAll()
                        // 认证相关端点
                        .requestMatchers(
                                "/auth/login/**",
                                "/auth/register/**",
                                "/auth/login/google",
                                "/auth/refresh",
                                "/auth/verification-code",
                                "/auth/verification-code/verify",
                                "/auth/verify-email",
                                "/auth/reset-password",
                                "/api/auth/login/**",
                                "/api/auth/register/**",
                                "/api/auth/login/google",
                                "/api/auth/refresh",
                                "/api/auth/verification-code",
                                "/api/auth/verification-code/verify",
                                "/api/auth/verify-email",
                                "/api/auth/reset-password",
                                "/mobile/auth/verification-code",
                                "/mobile/auth/register",
                                "/mobile/auth/phone/gateway-login",
                                "/api/mobile/auth/verification-code",
                                "/api/mobile/auth/register",
                                "/api/mobile/auth/phone/gateway-login"
                        ).permitAll()
                        // 其余需要认证
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(requestSignatureFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtAuthenticationFilterRegistration(
            JwtAuthenticationFilter filter
    ) {
        FilterRegistrationBean<JwtAuthenticationFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    public FilterRegistrationBean<RequestSignatureFilter> requestSignatureFilterRegistration(
            RequestSignatureFilter filter
    ) {
        FilterRegistrationBean<RequestSignatureFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

    /**
     * Writes a JSON error response in the same format as ApiResponse,
     * ensuring iOS clients can parse security filter chain errors properly.
     */
    private void writeJsonError(HttpServletResponse response, int httpStatus,
                                int code, String message) throws java.io.IOException {
        response.setStatus(httpStatus);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("code", code);
        body.put("message", message);
        body.put("data", null);
        response.getWriter().write(objectMapper.writeValueAsString(body));
        response.getWriter().flush();
    }
}
