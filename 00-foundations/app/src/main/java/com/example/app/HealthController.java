package com.example.app;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/health")
    public String health() {
        return "UP";
    }

    @GetMapping("/api/message")
    public String message() {
        return "Hello from DevOps Foundations";
    }
}

