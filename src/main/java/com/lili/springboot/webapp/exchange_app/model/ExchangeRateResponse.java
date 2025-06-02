package com.lili.springboot.webapp.exchange_app.model;

import java.math.BigDecimal;
import java.util.Map;

public class ExchangeRateResponse {

     private String base;
    private Map<String, BigDecimal> rates;

    public ExchangeRateResponse() {}

    public ExchangeRateResponse(String base, Map<String, BigDecimal> rates) {
        this.base = base;
        this.rates = rates;
    }

    public String getBase() { return base; }
    public void setBase(String base) { this.base = base; }
    
    public Map<String, BigDecimal> getRates() { return rates; }
    public void setRates(Map<String, BigDecimal> rates) { this.rates = rates; }

}
