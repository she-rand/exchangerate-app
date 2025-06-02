package com.lili.springboot.webapp.exchange_app;

import com.lili.springboot.webapp.exchange_app.controller.ExchangeRateController;
import com.lili.springboot.webapp.exchange_app.model.ExchangeRateResponse;
import com.lili.springboot.webapp.exchange_app.service.ExchangeRateService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ExchangeRateControllerTest {

    @Mock
    private ExchangeRateService exchangeRateService;
    
    private MockMvc mockMvc;
    
    @BeforeEach
    void setUp() {
        ExchangeRateController controller = new ExchangeRateController(exchangeRateService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }
    
    @Test
    void testGetExchangeRates_Success() throws Exception {
        // Arrange
        ExchangeRateResponse mockResponse = new ExchangeRateResponse(
            "EUR", 
            Map.of("USD", new BigDecimal("1.08"))
        );
        
        when(exchangeRateService.getExchangeRates(eq("EUR"), any()))
            .thenReturn(mockResponse);
        
        // Act & Assert - Ruta corregida según tu controller
        mockMvc.perform(get("/exchangeRates/EUR?symbols=USD"))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.base").value("EUR"))
            .andExpect(jsonPath("$.rates.USD").value(1.08));
        
        System.out.println("✅ Test exitoso con symbols válido");
    }
    
    @Test
    void testGetExchangeRates_BadRequest_MissingSymbols() throws Exception {
        // Test SIN parámetro symbols - debería dar 400
        mockMvc.perform(get("/exchangeRates/EUR"))
            .andExpect(status().isBadRequest());
        
        System.out.println("✅ Validación symbols faltante funciona");
    }
    
    @Test
    void testGetExchangeRates_BadRequest_EmptySymbols() throws Exception {
        // Test con symbols vacío - debería dar 400
        mockMvc.perform(get("/exchangeRates/EUR?symbols="))
            .andExpect(status().isBadRequest());
        
        System.out.println("✅ Validación symbols vacío funciona");
    }
    
    @Test
    void testGetExchangeRates_MultipleSymbols() throws Exception {
        // Test con múltiples símbolos
        ExchangeRateResponse mockResponse = new ExchangeRateResponse(
            "EUR", 
            Map.of(
                "USD", new BigDecimal("1.08"),
                "NZD", new BigDecimal("1.60")
            )
        );
        
        when(exchangeRateService.getExchangeRates(eq("EUR"), any()))
            .thenReturn(mockResponse);
        
        mockMvc.perform(get("/exchangeRates/EUR?symbols=USD,NZD"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.base").value("EUR"))
            .andExpect(jsonPath("$.rates.USD").value(1.08))
            .andExpect(jsonPath("$.rates.NZD").value(1.60));
        
        System.out.println("✅ Test múltiples símbolos exitoso");
    }
    
    @Test
    void testGetExchangeRates_EmptyResponse() throws Exception {
        // Test cuando el servicio retorna rates vacío
        ExchangeRateResponse mockResponse = new ExchangeRateResponse("EUR", Map.of());
        
        when(exchangeRateService.getExchangeRates(eq("EUR"), any()))
            .thenReturn(mockResponse);
        
        mockMvc.perform(get("/exchangeRates/EUR?symbols=USD"))
            .andExpect(status().isNotFound());
        
        System.out.println("✅ Manejo de respuesta vacía funciona");
    }
}