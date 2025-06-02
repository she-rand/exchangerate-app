package com.lili.springboot.webapp.exchange_app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.lili.springboot.webapp.exchange_app.model.MetricsResponse;
import com.lili.springboot.webapp.exchange_app.service.MetricsService;

public class MetricsServiceTest {

     private MetricsService metricsService;
    
    @BeforeEach
    void setUp() {
        metricsService = new MetricsService();
    }
    
    @Test
    void testMetricsCollection() {
        // Test initial state
        MetricsResponse metrics = metricsService.getMetrics();
        assertEquals(0, metrics.getTotalQueries());
        assertTrue(metrics.getApis().isEmpty());
        
        // Increment some metrics
        metricsService.incrementTotalQueries();
        metricsService.incrementApiRequests("testApi");
        metricsService.incrementApiResponses("testApi");
        
        // Verify metrics
        metrics = metricsService.getMetrics();
        assertEquals(1, metrics.getTotalQueries());
        assertEquals(1, metrics.getApis().size());
        assertEquals("testApi", metrics.getApis().get(0).getName());
        assertEquals(1, metrics.getApis().get(0).getTotalRequests());
        assertEquals(1, metrics.getApis().get(0).getTotalResponses());
    }
    
    @Test
    void testMetricsReset() {
        // Add some metrics
        metricsService.incrementTotalQueries();
        metricsService.incrementApiRequests("testApi");
        
        // Reset and verify
        metricsService.resetMetrics();
        MetricsResponse metrics = metricsService.getMetrics();
        
        assertEquals(0, metrics.getTotalQueries());
        assertTrue(metrics.getApis().isEmpty());
    }
}
