package com.lili.springboot.webapp.exchange_app.model;

import java.util.Objects;
import java.util.Set;

public class CacheKey {

     private final String baseCurrency;
    private final Set<String> symbols;

    public CacheKey(String baseCurrency, Set<String> symbols) {
        this.baseCurrency = baseCurrency;
        this.symbols = symbols;
    }

    public String getBaseCurrency() { return baseCurrency; }
    public Set<String> getSymbols() { return symbols; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        CacheKey cacheKey = (CacheKey) obj;
        return Objects.equals(baseCurrency, cacheKey.baseCurrency) &&
               Objects.equals(symbols, cacheKey.symbols);
    }

    @Override
    public int hashCode() {
        return Objects.hash(baseCurrency, symbols);
    }

    @Override
    public String toString() {
        return "CacheKey{baseCurrency='" + baseCurrency + "', symbols=" + symbols + "}";
    }

}
