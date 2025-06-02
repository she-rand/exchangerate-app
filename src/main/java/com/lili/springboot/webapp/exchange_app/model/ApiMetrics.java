package com.lili.springboot.webapp.exchange_app.model;

public class ApiMetrics {

     private String name;
    private long totalRequests;
    private long totalResponses;
    private long totalErrors;

    public ApiMetrics(String name) {
        this.name = name;
        this.totalRequests = 0;
        this.totalResponses = 0;
        this.totalErrors = 0;
    }

    public void incrementRequests() { this.totalRequests++; }
    public void incrementResponses() { this.totalResponses++; }
    public void incrementErrors() { this.totalErrors++; }

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public long getTotalRequests() { return totalRequests; }
    public void setTotalRequests(long totalRequests) { this.totalRequests = totalRequests; }
    
    public long getTotalResponses() { return totalResponses; }
    public void setTotalResponses(long totalResponses) { this.totalResponses = totalResponses; }
    
    public long getTotalErrors() { return totalErrors; }
    public void setTotalErrors(long totalErrors) { this.totalErrors = totalErrors; }

}
