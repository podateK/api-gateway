package com.gateway.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.common.circuitbreaker.configuration.CircuitBreakerConfigCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class Resilience4jConfig {

    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry() {
        io.github.resilience4j.circuitbreaker.CircuitBreakerConfig defaultConfig = io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.custom()
                .slidingWindowSize(10)
                .minimumNumberOfCalls(5)
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofSeconds(10))
                .permittedNumberOfCallsInHalfOpenState(3)
                .automaticTransitionFromOpenToHalfOpenEnabled(true)
                .recordExceptions(
                        java.io.IOException.class,
                        java.util.concurrent.TimeoutException.class,
                        org.springframework.web.client.ResourceAccessException.class
                )
                .ignoreExceptions(
                        IllegalArgumentException.class
                )
                .build();

        return CircuitBreakerRegistry.of(defaultConfig);
    }

    @Bean
    public CircuitBreakerConfigCustomizer userServiceCustomizer() {
        return CircuitBreakerConfigCustomizer.of("userServiceCircuitBreaker",
                builder -> builder
                        .slidingWindowSize(10)
                        .failureRateThreshold(50)
                        .waitDurationInOpenState(Duration.ofSeconds(10))
        );
    }

    @Bean
    public CircuitBreakerConfigCustomizer orderServiceCustomizer() {
        return CircuitBreakerConfigCustomizer.of("orderServiceCircuitBreaker",
                builder -> builder
                        .slidingWindowSize(15)
                        .failureRateThreshold(40)
                        .waitDurationInOpenState(Duration.ofSeconds(15))
        );
    }

    @Bean
    public CircuitBreakerConfigCustomizer productServiceCustomizer() {
        return CircuitBreakerConfigCustomizer.of("productServiceCircuitBreaker",
                builder -> builder
                        .slidingWindowSize(10)
                        .failureRateThreshold(60)
                        .waitDurationInOpenState(Duration.ofSeconds(8))
        );
    }
}
