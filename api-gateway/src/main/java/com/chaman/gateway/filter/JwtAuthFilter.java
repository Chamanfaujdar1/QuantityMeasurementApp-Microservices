package com.chaman.gateway.filter;

import com.chaman.gateway.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthFilter extends AbstractGatewayFilterFactory<JwtAuthFilter.Config> {

    @Autowired
    private JwtUtil jwtUtil;

    public JwtAuthFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getURI().getPath();

            // Allow auth endpoints without token
            if (path.startsWith("/api/v1/auth/")) {
                return chain.filter(exchange);
            }

            // Allow quantities endpoints without token as operations are public
            if (path.startsWith("/api/v1/quantities/")) {
                return chain.filter(exchange);
            }

            // Allow OAuth2 endpoints for Google Login to pass through
            if (path.startsWith("/oauth2/") || path.startsWith("/login/oauth2/")) {
                return chain.filter(exchange);
            }

            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            String token = authHeader.substring(7);

            if (!jwtUtil.isTokenValid(token)) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            // Forward username as header to downstream services
            String username = jwtUtil.extractUsername(token);
            exchange = exchange.mutate()
                    .request(r -> r.header("X-Auth-User", username))
                    .build();

            return chain.filter(exchange);
        };
    }

    public static class Config {}
}
