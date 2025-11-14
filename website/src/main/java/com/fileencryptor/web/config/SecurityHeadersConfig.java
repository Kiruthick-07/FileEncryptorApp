package com.fileencryptor.web.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
public class SecurityHeadersConfig {

    @Bean
    public FilterRegistrationBean<OncePerRequestFilter> securityHeadersFilter() {
        final String contentSecurityPolicy = String.join(" ",
                "default-src 'self';",
                "script-src 'self';",
                "style-src 'self' 'unsafe-inline' https://cdnjs.cloudflare.com;",
                "font-src 'self' data: https://cdnjs.cloudflare.com;",
                "img-src 'self' data: blob:;",
                "connect-src 'self';",
                "object-src 'none';",
                "base-uri 'self';",
                "frame-ancestors 'none'");

        OncePerRequestFilter filter = new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                    throws ServletException, IOException {
                // Core security headers
                response.setHeader("Content-Security-Policy", contentSecurityPolicy);
                response.setHeader("X-Content-Type-Options", "nosniff");
                response.setHeader("X-Frame-Options", "DENY");
                response.setHeader("Referrer-Policy", "no-referrer");
                response.setHeader("Permissions-Policy", "camera=(), microphone=(), geolocation=()");
                // HSTS is only effective over HTTPS; harmless locally
                response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");

                filterChain.doFilter(request, response);
            }
        };

        FilterRegistrationBean<OncePerRequestFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }
}
