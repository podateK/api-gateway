package com.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/{service}")
    public ResponseEntity<Map<String, Object>> fallback(@PathVariable String service) {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "error", "Service unavailable",
                        "service", service,
                        "timestamp", LocalDateTime.now().toString(),
                        "message", "The requested service is temporarily unavailable. Please try again later."
                ));
    }
}
