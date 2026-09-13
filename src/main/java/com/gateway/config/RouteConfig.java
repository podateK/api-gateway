package com.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfig {

    @Bean
    public RedisRateLimiter redisRateLimiter() {
        return new RedisRateLimiter(10, 20, 1);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder, RedisRateLimiter rateLimiter) {
        return builder.routes()
                .route("auth-service", r -> r
                        .path("/api/auth/v2/**")
                        .filters(f -> f
                                .stripPrefix(2)
                                .addRequestHeader("X-Gateway-Source", "api-gateway")
                        )
                        .uri("http://localhost:8084")
                )
                .route("notification-service", r -> r
                        .path("/api/notifications/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .circuitBreaker(c -> c
                                        .setName("notificationServiceCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/notification-service")
                                )
                                .retry(config -> config
                                        .setRetries(3)
                                        .setBackoff(
                                                java.time.Duration.ofMillis(100),
                                                java.time.Duration.ofSeconds(1),
                                                2, true
                                        )
                                )
                        )
                        .uri("http://localhost:8085")
                )
                .route("analytics-service", r -> r
                        .path("/api/analytics/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .requestRateLimiter(config -> config
                                        .setRateLimiter(rateLimiter)
                                        .setKeyResolver(ipKeyResolver())
                                )
                        )
                        .uri("http://localhost:8086")
                )
                .build();
    }

    @Bean
    public org.springframework.cloud.gateway.filter.ratelimit.KeyResolver ipKeyResolver() {
        return exchange -> {
            String ip = exchange.getRequest().getRemoteAddress() != null
                    ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
                    : "anonymous";
            return reactor.core.publisher.Mono.just(ip);
        };
    }
}
