package com.lili.springboot.webapp.exchange_app.model;

import java.util.List;

public class MetricsResponse {

    private long totalQueries;
    private List<ApiMetrics> apis;

    public MetricsResponse() {}

    public MetricsResponse(long totalQueries, List<ApiMetrics> apis) {
        this.totalQueries = totalQueries;
        this.apis = apis;
    }

    public long getTotalQueries() { return totalQueries; }
    public void setTotalQueries(long totalQueries) { this.totalQueries = totalQueries; }
    
    public List<ApiMetrics> getApis() { return apis; }
    public void setApis(List<ApiMetrics> apis) { this.apis = apis; }

}
