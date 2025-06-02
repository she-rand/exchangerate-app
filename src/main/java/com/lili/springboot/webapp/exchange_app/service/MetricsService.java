package com.lili.springboot.webapp.exchange_app.service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.lili.springboot.webapp.exchange_app.model.ApiMetrics;
import com.lili.springboot.webapp.exchange_app.model.MetricsResponse;

@Service
public class MetricsService {
     private final AtomicLong totalQueries = new AtomicLong(0);
    private final ConcurrentHashMap<String, ApiMetrics> apiMetrics = new ConcurrentHashMap<>();
    
    public void incrementTotalQueries() {
        totalQueries.incrementAndGet();
    }
    
    public void incrementApiRequests(String apiName) {
        apiMetrics.computeIfAbsent(apiName, ApiMetrics::new).incrementRequests();
    }
    
    public void incrementApiResponses(String apiName) {
        apiMetrics.computeIfAbsent(apiName, ApiMetrics::new).incrementResponses();
    }
    
    public void incrementApiErrors(String apiName) {
        apiMetrics.computeIfAbsent(apiName, ApiMetrics::new).incrementErrors();
    }
    
    public MetricsResponse getMetrics() {
        List<ApiMetrics> apis = apiMetrics.values().stream()
            .collect(Collectors.toList());
        
        return new MetricsResponse(totalQueries.get(), apis);
    }
    
    // Reset all metrics (useful for testing)
    public void resetMetrics() {
        totalQueries.set(0);
        apiMetrics.clear();
    }
 
}
