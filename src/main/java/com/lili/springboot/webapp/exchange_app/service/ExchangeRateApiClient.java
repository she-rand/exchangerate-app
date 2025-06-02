package com.lili.springboot.webapp.exchange_app.service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

public interface ExchangeRateApiClient {
     String getApiName();
    Map<String, BigDecimal> getExchangeRates(String baseCurrency, Set<String> symbols);

}















