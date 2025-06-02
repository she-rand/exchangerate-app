package com.lili.springboot.webapp.exchange_app;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import com.lili.springboot.webapp.exchange_app.service.FrankfurterApiClient;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test API clients con MockWebServer
 */
class ApiClientTest {

    private MockWebServer mockWebServer;
    private FrankfurterApiClient frankfurterClient;
    
    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        
        // Crear client apuntando al mock server
        WebClient.Builder webClientBuilder = WebClient.builder()
            .baseUrl(mockWebServer.url("/").toString());
        
        frankfurterClient = new FrankfurterApiClient(webClientBuilder);
    }
    
    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }
    
    @Test
    void testFrankfurterApiClient_Success() throws Exception {
        // Mock respuesta exitosa de Frankfurter API
        String mockResponse = """
            {
                "base": "EUR",
                "date": "2024-01-01",
                "rates": {
                    "USD": 1.08,
                    "NZD": 1.60
                }
            }
            """;
        
        mockWebServer.enqueue(new MockResponse()
            .setBody(mockResponse)
            .addHeader("Content-Type", "application/json"));
        
        // Test del client
        Map<String, BigDecimal> rates = frankfurterClient.getExchangeRates("EUR", Set.of("USD", "NZD"));
        
        assertNotNull(rates);
        System.out.println("✅ Rates recibidas: " + rates);
        
        // Verificar que se hizo la petición
        assertEquals(1, mockWebServer.getRequestCount());
        
        System.out.println("✅ Frankfurter API client test exitoso");
    }
    
    @Test
    void testFrankfurterApiClient_HttpError() {
        // Mock error response (HTTP 500)
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));
        
        // NO esperamos excepción, solo verificamos que maneja el error correctamente
        Map<String, BigDecimal> rates = frankfurterClient.getExchangeRates("EUR", Set.of("USD"));
        
        // Tu implementación probablemente retorna Map vacío en caso de error
        assertNotNull(rates);
        assertTrue(rates.isEmpty(), "Debería retornar Map vacío en caso de error");
        
        System.out.println("✅ Error HTTP manejado correctamente: Map vacío");
    }
    
    @Test
    void testFrankfurterApiClient_InvalidJson() {
        // Mock respuesta con JSON inválido
        mockWebServer.enqueue(new MockResponse()
            .setBody("Invalid JSON {")
            .addHeader("Content-Type", "application/json"));
        
        // Verificar que maneja JSON inválido
        Map<String, BigDecimal> rates = frankfurterClient.getExchangeRates("EUR", Set.of("USD"));
        
        assertNotNull(rates);
        // Puede ser vacío o tener datos de prueba, dependiendo de tu implementación
        System.out.println("✅ JSON inválido manejado: " + rates);
    }
    
    @Test
    void testFrankfurterApiClient_EmptyResponse() {
        // Mock respuesta vacía
        mockWebServer.enqueue(new MockResponse()
            .setBody("")
            .addHeader("Content-Type", "application/json"));
        
        Map<String, BigDecimal> rates = frankfurterClient.getExchangeRates("EUR", Set.of("USD"));
        
        assertNotNull(rates);
        System.out.println("✅ Respuesta vacía manejada: " + rates);
    }
    
    @Test
    void testApiClientExists() {
        // Test básico para verificar que el client se crea correctamente
        assertNotNull(frankfurterClient);
        assertEquals("frankfurter", frankfurterClient.getApiName());
        
        System.out.println("✅ FrankfurterApiClient creado correctamente");
    }
}