package com.lili.springboot.webapp.exchange_app.controller;

import com.lili.springboot.webapp.exchange_app.model.ExchangeRateResponse;
import com.lili.springboot.webapp.exchange_app.service.ExchangeRateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/exchangeRates")
public class ExchangeRateController {
    
    private static final Logger logger = LoggerFactory.getLogger(ExchangeRateController.class);
    
    private final ExchangeRateService exchangeRateService;
    
    public ExchangeRateController(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }
    
    @GetMapping("/{baseCurrency}")
    public ResponseEntity<ExchangeRateResponse> getExchangeRates(
            @PathVariable String baseCurrency,
            @RequestParam String symbols) {
        
        try {
            logger.info("Received request for base: {}, symbols: {}", baseCurrency, symbols);
            
            // Validate inputs
            if (baseCurrency == null || baseCurrency.trim().isEmpty()) {
                logger.warn("Invalid base currency: {}", baseCurrency);
                return ResponseEntity.badRequest().build();
            }
            
            if (symbols == null || symbols.trim().isEmpty()) {
                logger.warn("Invalid symbols parameter: {}", symbols);
                return ResponseEntity.badRequest().build();
            }
            
            // Parse symbols parameter (e.g., "USD,NZD,GBP")
            Set<String> symbolSet = Arrays.stream(symbols.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
            
            if (symbolSet.isEmpty()) {
                logger.warn("No valid symbols found in: {}", symbols);
                return ResponseEntity.badRequest().build();
            }
            
            // Get exchange rates
            ExchangeRateResponse response = exchangeRateService.getExchangeRates(baseCurrency, symbolSet);
            
            if (response.getRates().isEmpty()) {
                logger.warn("No exchange rates found for base: {}, symbols: {}", baseCurrency, symbols);
                return ResponseEntity.notFound().build();
            }
            
            logger.info("Successfully returned rates for base: {}, symbols: {}", baseCurrency, symbols);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error processing exchange rate request", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}