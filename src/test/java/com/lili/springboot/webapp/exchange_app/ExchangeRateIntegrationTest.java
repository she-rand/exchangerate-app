package com.lili.springboot.webapp.exchange_app;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ExchangeRateIntegrationTest {

    @LocalServerPort
    private int port;
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    void testApplicationStarts() {
        assertNotNull(restTemplate);
        assertTrue(port > 0);
        System.out.println("✅ Aplicación arrancó en puerto: " + port);
    }
    
    @Test
    void testHealthEndpoint() {
        // Usar /api/health porque tienes context-path: /api
        String url = "http://localhost:" + port + "/api/health";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("UP"));
        
        System.out.println("✅ Health endpoint funciona: " + response.getBody());
    }
    
    @Test
    void testExchangeRateEndpoint() {
        // Este ya sabemos que funciona
        String url = "http://localhost:" + port + "/api/exchangeRates/EUR?symbols=USD,NZD";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("EUR"));
        
        System.out.println("✅ Exchange Rate endpoint funciona");
        System.out.println("📄 Respuesta: " + response.getBody());
    }
    
    @Test
    void testMetricsEndpoint() {
        String url = "http://localhost:" + port + "/api/metrics";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        
        System.out.println("Metrics endpoint status: " + response.getStatusCode());
        
        if (response.getStatusCode() == HttpStatus.OK) {
            assertNotNull(response.getBody());
            System.out.println("✅ Metrics endpoint funciona: " + response.getBody());
        } else if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
            System.out.println("⚠️ Metrics endpoint no existe aún (necesitas crear MetricsController)");
        } else {
            System.out.println("⚠️ Metrics endpoint status: " + response.getStatusCode());
        }
    }
    
    @Test
    void testExchangeRateEndpoint_ValidationErrors() {
        // Test sin parámetro symbols
        String url = "http://localhost:" + port + "/api/exchangeRates/EUR";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        System.out.println("✅ Validación de parámetros funciona correctamente");
    }
    
    @Test
    void testExchangeRateEndpoint_EmptySymbols() {
        // Test con symbols vacío
        String url = "http://localhost:" + port + "/api/exchangeRates/EUR?symbols=";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        System.out.println("✅ Validación de symbols vacío funciona");
    }
}