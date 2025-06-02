package com.lili.springboot.webapp.exchange_app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lili.springboot.webapp.exchange_app.model.ExchangeRateResponse;
import com.lili.springboot.webapp.exchange_app.service.ExchangeRateApiClient;
import com.lili.springboot.webapp.exchange_app.service.ExchangeRateService;
import com.lili.springboot.webapp.exchange_app.service.MetricsService;

/**
 * Unit tests for ExchangeRateService
 * 
 * @ExtendWith(MockitoExtension.class) - Enables Mockito for creating mock objects
 * This is essential for unit testing - we mock external dependencies
 */
@ExtendWith(MockitoExtension.class)
public class ExchangeRateServiceTest {

     @Mock
    private ExchangeRateApiClient mockApiClient1;
    
    @Mock
    private ExchangeRateApiClient mockApiClient2;
    
    @Mock
    private MetricsService mockMetricsService;
    
    private ExchangeRateService exchangeRateService;
    
    @BeforeEach
    void setUp() {
        // Create service with mock dependencies
        List<ExchangeRateApiClient> apiClients = Arrays.asList(mockApiClient1, mockApiClient2);
        exchangeRateService = new ExchangeRateService(apiClients, mockMetricsService);
        
        // Setup mock names
        when(mockApiClient1.getApiName()).thenReturn("api1");
        when(mockApiClient2.getApiName()).thenReturn("api2");
    }
    
    @Test
    void testGetExchangeRates_Success() {
        // Arrange - Set up test data
        String baseCurrency = "EUR";
        Set<String> symbols = Set.of("USD", "NZD");
        
        // Mock responses from both APIs
        Map<String, BigDecimal> rates1 = Map.of(
            "USD", new BigDecimal("1.08"),
            "NZD", new BigDecimal("1.60")
        );
        
        Map<String, BigDecimal> rates2 = Map.of(
            "USD", new BigDecimal("1.07"),
            "NZD", new BigDecimal("1.58")
        );
        
        when(mockApiClient1.getExchangeRates(baseCurrency, symbols)).thenReturn(rates1);
        when(mockApiClient2.getExchangeRates(baseCurrency, symbols)).thenReturn(rates2);
        
        // Act - Execute the method we're testing
        ExchangeRateResponse response = exchangeRateService.getExchangeRates(baseCurrency, symbols);
        
        // Assert - Verify the results
        assertNotNull(response);
        assertEquals("EUR", response.getBase());
        assertEquals(2, response.getRates().size());
        
        // Verify averages: (1.08 + 1.07) / 2 = 1.075
        assertEquals(new BigDecimal("1.075000"), response.getRates().get("USD"));
        assertEquals(new BigDecimal("1.590000"), response.getRates().get("NZD"));
        
        // Verify metrics were called
        verify(mockMetricsService).incrementTotalQueries();
        verify(mockMetricsService, times(2)).incrementApiRequests(anyString());
        verify(mockMetricsService, times(2)).incrementApiResponses(anyString());
    }
    
    @Test
    void testGetExchangeRates_OneApiFails() {
        // Test resilience - one API fails, other succeeds
        String baseCurrency = "EUR";
        Set<String> symbols = Set.of("USD");
        
        Map<String, BigDecimal> rates = Map.of("USD", new BigDecimal("1.08"));
        
        when(mockApiClient1.getExchangeRates(baseCurrency, symbols)).thenReturn(rates);
        when(mockApiClient2.getExchangeRates(baseCurrency, symbols))
            .thenThrow(new RuntimeException("API failure"));
        
        // Should still return result from working API
        ExchangeRateResponse response = exchangeRateService.getExchangeRates(baseCurrency, symbols);
        
        assertNotNull(response);
        assertEquals(new BigDecimal("1.080000"), response.getRates().get("USD"));
        
        // Verify error was tracked
        verify(mockMetricsService).incrementApiErrors("api2");
    }
    
    @Test
    void testCaching() {
        // Test that subsequent calls use cache
        String baseCurrency = "EUR";
        Set<String> symbols = Set.of("USD");
        
        Map<String, BigDecimal> rates = Map.of("USD", new BigDecimal("1.08"));
        when(mockApiClient1.getExchangeRates(baseCurrency, symbols)).thenReturn(rates);
        when(mockApiClient2.getExchangeRates(baseCurrency, symbols)).thenReturn(rates);
        
        // First call
        ExchangeRateResponse response1 = exchangeRateService.getExchangeRates(baseCurrency, symbols);
        
        // Second call (should use cache)
        ExchangeRateResponse response2 = exchangeRateService.getExchangeRates(baseCurrency, symbols);
        
        // Should be identical
        assertEquals(response1.getRates(), response2.getRates());
        
        // API clients should only be called once (first time)
        verify(mockApiClient1, times(1)).getExchangeRates(baseCurrency, symbols);
        verify(mockApiClient2, times(1)).getExchangeRates(baseCurrency, symbols);
        
        // Metrics should be incremented twice (once per query)
        verify(mockMetricsService, times(2)).incrementTotalQueries();
    }

}
