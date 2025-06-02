package com.lili.springboot.webapp.exchange_app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lili.springboot.webapp.exchange_app.model.MetricsResponse;
import com.lili.springboot.webapp.exchange_app.service.MetricsService;

@RestController
@RequestMapping("/metrics")
public class MetricsController {
     private final MetricsService metricsService;
    
    
    public MetricsController(MetricsService metricsService) {
        this.metricsService = metricsService;
    }
    
    @GetMapping
    public ResponseEntity<MetricsResponse> getMetrics() {
        MetricsResponse metrics = metricsService.getMetrics();
        return ResponseEntity.ok(metrics);
    }

}
