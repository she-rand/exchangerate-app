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
public class FrankfurterApiClient implements ExchangeRateApiClient {
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    
   public FrankfurterApiClient(WebClient.Builder webClientBuilder) {
    this.webClient = webClientBuilder
        .build();  // ← Sin .baseUrl(), usa la URL que ya viene en el builder
    this.objectMapper = new ObjectMapper();
    }
    
    @Override
    public String getApiName() {
        return "frankfurter";
    }
    
    @Override
    public Map<String, BigDecimal> getExchangeRates(String baseCurrency, Set<String> symbols) {
        try {
            String symbolsParam = String.join(",", symbols);
            
            String jsonResponse = webClient.get()
                .uri("/latest?base={base}&symbols={symbols}", baseCurrency, symbolsParam)
                .retrieve()
                .bodyToMono(String.class)
                .block();
            
            // Parseamos el JSON response
            Map<String, BigDecimal> rates = new HashMap<>();
            if (jsonResponse != null && !jsonResponse.isEmpty()) {
                JsonNode rootNode = objectMapper.readTree(jsonResponse);
                JsonNode ratesNode = rootNode.get("rates");
                
                if (ratesNode != null) {
                    for (String symbol : symbols) {
                        JsonNode rateNode = ratesNode.get(symbol);
                        if (rateNode != null) {
                            rates.put(symbol, new BigDecimal(rateNode.asText()));
                        }
                    }
                }
            }
            
            System.out.println("Frankfurter API response: " + jsonResponse);
            return rates;
            
        } catch (Exception e) {
            System.err.println("Error fetching from Frankfurter: " + e.getMessage());
            return new HashMap<>();
        }
    }

}
