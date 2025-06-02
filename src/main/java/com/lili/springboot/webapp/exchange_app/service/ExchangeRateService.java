package com.lili.springboot.webapp.exchange_app.service;
import com.lili.springboot.webapp.exchange_app.model.CacheKey;
import com.lili.springboot.webapp.exchange_app.model.ExchangeRateResponse;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ExchangeRateService {
   private static final Logger logger = (Logger) LoggerFactory.getLogger(ExchangeRateService.class);
    
    private final List<ExchangeRateApiClient> apiClients;
    private final MetricsService metricsService;
    
    // Simple in-memory cache
    private final Map<CacheKey, ExchangeRateResponse> cache = new ConcurrentHashMap<>();
    
    public ExchangeRateService(List<ExchangeRateApiClient> apiClients, MetricsService metricsService) {
        this.apiClients = apiClients;
        this.metricsService = metricsService;
        logger.info("Initialized ExchangeRateService with {} API clients", apiClients.size());
    }
    
    public ExchangeRateResponse getExchangeRates(String baseCurrency, Set<String> symbols) {
        // Normalize inputs
        String normalizedBase = baseCurrency.toUpperCase();
        Set<String> normalizedSymbols = symbols.stream()
            .map(String::toUpperCase)
            .collect(Collectors.toSet());
        
        // Check cache first
        CacheKey cacheKey = new CacheKey(normalizedBase, normalizedSymbols);
        if (cache.containsKey(cacheKey)) {
            logger.info("Cache hit for {}", cacheKey);
            metricsService.incrementTotalQueries();
            return cache.get(cacheKey);
        }
        
        logger.info("Cache miss for {}, fetching from APIs", cacheKey);
        
        // Fetch from all APIs and collect results
        Map<String, List<BigDecimal>> allRates = new HashMap<>();
        
        // Initialize the map with empty lists for each symbol
        for (String symbol : normalizedSymbols) {
            allRates.put(symbol, new ArrayList<>());
        }
        
        // Query each API
        for (ExchangeRateApiClient client : apiClients) {
            try {
                logger.info("Fetching rates from {}", client.getApiName());
                metricsService.incrementApiRequests(client.getApiName());
                
                Map<String, BigDecimal> rates = client.getExchangeRates(normalizedBase, normalizedSymbols);
                
                metricsService.incrementApiResponses(client.getApiName());
                
                // Collect rates for averaging
                for (Map.Entry<String, BigDecimal> entry : rates.entrySet()) {
                    String symbol = entry.getKey().toUpperCase();
                    if (allRates.containsKey(symbol)) {
                        allRates.get(symbol).add(entry.getValue());
                    }
                }
                
            } catch (Exception e) {
                logger.error("Failed to fetch rates from {}: {}", client.getApiName(), e.getMessage());
                metricsService.incrementApiErrors(client.getApiName());
            }
        }
        
        // Calculate average rates
        Map<String, BigDecimal> averageRates = new HashMap<>();
        for (Map.Entry<String, List<BigDecimal>> entry : allRates.entrySet()) {
            String symbol = entry.getKey();
            List<BigDecimal> rates = entry.getValue();
            
            if (!rates.isEmpty()) {
                BigDecimal average = rates.stream()
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(rates.size()), 6, RoundingMode.HALF_UP);
                averageRates.put(symbol, average);
            } else {
                logger.warn("No rates found for symbol: {}", symbol);
            }
        }
        
        // Create response
        ExchangeRateResponse response = new ExchangeRateResponse(normalizedBase, averageRates);
        
        // Cache the result
        cache.put(cacheKey, response);
        metricsService.incrementTotalQueries();
        
        logger.info("Successfully fetched and cached rates for {}", cacheKey);
        return response;
    }
    
    // Method to clear cache (useful for testing or manual cache management)
    public void clearCache() {
        cache.clear();
        logger.info("Cache cleared");
    }
    
    // Method to get cache size (useful for monitoring)
    public int getCacheSize() {
        return cache.size();
    }

}
