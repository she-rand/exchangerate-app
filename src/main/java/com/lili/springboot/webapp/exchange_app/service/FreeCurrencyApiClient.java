package com.lili.springboot.webapp.exchange_app.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class FreeCurrencyApiClient implements ExchangeRateApiClient {
    
   private final WebClient webClient;
    private final ObjectMapper objectMapper;
    
    public FreeCurrencyApiClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
            .baseUrl("https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@latest/v1/currencies")
            .build();
        this.objectMapper = new ObjectMapper();
    }
    
    @Override
    public String getApiName() {
        return "freeCurrencyRates";
    }
    
    @Override
    public Map<String, BigDecimal> getExchangeRates(String baseCurrency, Set<String> symbols) {
        try {
            String jsonResponse = webClient.get()
                .uri("/{baseCurrency}.json", baseCurrency.toLowerCase())
                .retrieve()
                .bodyToMono(String.class)
                .block();
            
            // Parseamos el JSON response
            Map<String, BigDecimal> rates = new HashMap<>();
            if (jsonResponse != null && !jsonResponse.isEmpty()) {
                JsonNode rootNode = objectMapper.readTree(jsonResponse);
                
                // Esta API tiene estructura: {"date": "2024-01-01", "eur": {"usd": 1.08, "nzd": 1.6}}
                JsonNode baseCurrencyNode = rootNode.get(baseCurrency.toLowerCase());
                
                if (baseCurrencyNode != null) {
                    for (String symbol : symbols) {
                        JsonNode rateNode = baseCurrencyNode.get(symbol.toLowerCase());
                        if (rateNode != null) {
                            rates.put(symbol.toUpperCase(), new BigDecimal(rateNode.asText()));
                        }
                    }
                }
            }
            
            System.out.println("Free Currency API response: " + jsonResponse);
            return rates;
            
        } catch (Exception e) {
            System.err.println("Error fetching from Free Currency API: " + e.getMessage());
            return new HashMap<>();
        }
    }
}