# Exchange Rate Service

A SpringBoot application that fetches exchange rates from multiple APIs, averages the results, and provides caching and metrics.

## Features

- **Multi-API Integration**: Fetches rates from Frankfurter and Free Currency APIs
- **Rate Averaging**: Calculates average exchange rates from multiple sources for increased accuracy
- **Intelligent Caching**: Caches results to avoid redundant API calls
- **Comprehensive Metrics**: Tracks API usage, response times, and error rates
- **REST API**: Clean REST endpoints for integration
- **Resilient**: Continues to work even if some external APIs fail

## API Endpoints

### Get Exchange Rates
```http
GET /api/exchangeRates/{baseCurrency}?symbols={SYMBOL1,SYMBOL2,...}
```

**Example:**
```http
GET /api/exchangeRates/EUR?symbols=USD,NZD,GBP
```

**Response:**
```json
{
  "base": "EUR",
  "rates": {
    "USD": 1.078588,
    "NZD": 1.599893,
    "GBP": 0.834567
  }
}
```

### Get Metrics
```http
GET /api/metrics
```

**Response:**
```json
{
  "totalQueries": 50,
  "apis": [
    {
      "name": "frankfurter",
      "totalRequests": 50,
      "totalResponses": 48,
      "totalErrors": 2
    },
    {
      "name": "freeCurrencyRates",
      "totalRequests": 50,
      "totalResponses": 50,
      "totalErrors": 0
    }
  ]
}
```

### Health Check
```http
GET /api/health
```

## Architecture Overview

### Components

1. **Controllers** (`controller/`)
   - `ExchangeRateController`: Main API endpoint
   - `MetricsController`: Metrics reporting
   - `HealthController`: Health check

2. **Services** (`service/`)
   - `ExchangeRateService`: Core business logic, caching, averaging
   - `MetricsService`: Metrics collection and reporting
   - `ExchangeRateApiClient`: Interface for external API clients

3. **API Clients** (`service/`)
   - `FrankfurterApiClient`: Integrates with Frankfurter API
   - `FreeCurrencyApiClient`: Integrates with Free Currency API

4. **Models** (`model/`)
   - `ExchangeRateResponse`: API response format
   - `MetricsResponse`: Metrics response format
   - `CacheKey`: Cache key for request deduplication

### Design Decisions

#### Why SpringBoot?
- **Auto-configuration**: Minimal setup for web services, JSON handling, HTTP clients
- **Dependency Injection**: Clean separation of concerns, easy testing
- **Built-in Testing**: Excellent testing framework with mocking support
- **Production Ready**: Built-in metrics, health checks, and monitoring

#### Caching Strategy
- **In-Memory Cache**: Simple `ConcurrentHashMap` for fast access
- **Composite Keys**: Cache by combination of base currency and target symbols
- **Thread-Safe**: Uses concurrent collections for multi-threaded access

**Alternative Considerations:**
- Redis for distributed caching
- TTL-based expiration for stale data
- Cache size limits and LRU eviction

#### Error Handling
- **Graceful Degradation**: Continue with partial data if some APIs fail
- **Comprehensive Logging**: Track all failures for debugging
- **Metrics Collection**: Monitor API reliability over time

#### Rate Averaging Algorithm
```java
// Simple arithmetic mean
BigDecimal average = rates.stream()
    .reduce(BigDecimal.ZERO, BigDecimal::add)
    .divide(BigDecimal.valueOf(rates.size()), 6, RoundingMode.HALF_UP);
```

**Alternative Approaches:**
- Weighted averages based on API reliability
- Median instead of mean to handle outliers
- Configurable aggregation strategies

#### HTTP Client Choice
- **WebClient over RestTemplate**: Non-blocking, reactive, better error handling
- **Timeout Configuration**: Prevent hanging requests
- **Connection Pooling**: Efficient resource usage

## Technology Stack

- **Java 17**: Modern Java features, records, enhanced switch
- **SpringBoot 3.5**: Latest stable version with Spring 6
- **Maven**: Dependency management and build automation
- **Jackson**: JSON serialization/deserialization
- **JUnit 5**: Modern testing framework
- **Mockito**: Mocking framework for unit tests
- **MockWebServer**: HTTP client testing

## Running the Application

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher

### Build and Run
```bash
# Clone the repository
git clone <repository-url>
cd exchange-rate-service

# Build the application
mvn clean compile

# Run tests
mvn test

# Start the application
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Docker (Optional)
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/exchange-rate-service-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

```bash
# Build image
docker build -t exchange-rate-service .

# Run container
docker run -p 8080:8080 exchange-rate-service
```

## Testing

### Unit Tests
```bash
mvn test
```

### Integration Tests
```bash
mvn verify
```

### Manual Testing
```bash
# Test exchange rates
curl "http://localhost:8080/api/exchangeRates/EUR?symbols=USD,NZD"

# Test metrics
curl "http://localhost:8080/api/metrics"

# Test health
curl "http://localhost:8080/api/health"
```

# Exchange Rate Service

A simple SpringBoot application that fetches exchange rates from multiple APIs, calculates averages, and provides easy-to-use REST endpoints.

## What does this application do?

- **Fetches exchange rates** from two different APIs (Frankfurter and Free Currency)
- **Calculates averages** for better accuracy
- **Caches results** for faster responses
- **Collects usage metrics**
- **Works even if one API fails**

## How to use the application

### Get exchange rates
```http
GET /api/exchangeRates/EUR?symbols=USD,NZD,GBP
```

**Example:**
```bash
curl "http://localhost:8080/api/exchangeRates/EUR?symbols=USD,NZD"
```

**Response:**
```json
{
  "base": "EUR",
  "rates": {
    "USD": 1.078588,
    "NZD": 1.599893
  }
}
```

### View usage statistics
```http
GET /api/metrics
```

**Response:**
```json
{
  "totalQueries": 50,
  "apis": [
    {
      "name": "frankfurter",
      "totalRequests": 50,
      "totalResponses": 48,
      "totalErrors": 2
    }
  ]
}
```

### Check if it's working
```http
GET /api/health
```

## How to run the application

### Requirements
- Java 17 or higher
- Maven 3.6 or higher

### Steps
```bash
# 1. Clone or download the project
cd exchange-rate-service

# 2. Compile
mvn clean compile

# 3. Run
mvn spring-boot:run

# 4. Test in your browser
# http://localhost:8080/api/health
```

### Test with curl
```bash
# Check it's working
curl "http://localhost:8080/api/health"

# Get exchange rates
curl "http://localhost:8080/api/exchangeRates/EUR?symbols=USD,NZD"

# View statistics
curl "http://localhost:8080/api/metrics"
```

## Project structure

```
src/main/java/com/lili/springboot/webapp/exchange_app/
├── ExchangeRateApplication.java    # Main application
├── controller/                     # REST endpoints
│   ├── ExchangeRateController.java # Main endpoint
│   ├── HealthController.java       # Application status
│   └── MetricsController.java      # Usage statistics
├── service/                        # Business logic
│   ├── ExchangeRateService.java    # Main service
│   ├── MetricsService.java         # Metrics collection
│   └── FrankfurterApiClient.java   # External API client
└── model/                          # Data models
    ├── ExchangeRateResponse.java   # API response
    └── ApiMetrics.java             # API metrics
```

## Run tests
```bash
# All tests
mvn test

# Specific test
mvn test -Dtest=ExchangeRateIntegrationTest
```

## Technical features

### Why is it reliable?
- **Multiple sources:** If one API fails, it keeps working
- **Averaged results:** Better accuracy than using a single source
- **Smart caching:** Avoids unnecessary calls
- **Detailed metrics:** Performance monitoring

### APIs it uses
1. **Frankfurter API** - European Central Bank data
2. **Free Currency API** - Free API with multiple currencies

### Technologies
- **SpringBoot 3.1.5** - Main framework
- **Java 17** - Programming language
- **Maven** - Dependency management
- **JUnit 5** - Automated testing

## Configuration

### Change port
In `application.yml`:
```yaml
server:
  port: 9090  # Change from 8080 to 9090
```

### Change routes
In `application.yml`:
```yaml
server:
  servlet:
    context-path: /myapp  # Change from /api to /myapp
```

## Common issues

### Application won't start
```bash
# Check Java
java -version

# Check Maven
mvn -version

# Clean and recompile
mvn clean compile
```

### Can't find endpoints
- Make sure you use `/api/` in the routes
- Example: `http://localhost:8080/api/health`

### External APIs fail
- This is normal, the application keeps working
- Check `/api/metrics` to see the status

## Future improvements

### Features
- [ ] More exchange rate APIs
- [ ] Historical rates
- [ ] Alerts for significant changes
- [ ] Direct conversion API

### Technical
- [ ] Database for persistence
- [ ] Redis for distributed cache
- [ ] Docker for deployment
- [ ] Monitoring with Prometheus

## License

MIT License - Use it freely for learning and personal projects.

---

## Need help?

- **Compilation error:** Check Java 17 and Maven
- **404 on endpoints:** Use `/api/` as prefix
- **Tests failing:** Run `mvn clean test`
- **APIs not responding:** This is normal, check `/api/metrics`

Enjoy exploring SpringBoot! 🚀 8080

app:
  exchange-rate:
    cache-ttl: 300      # Cache TTL in seconds
    timeout: 5000       # HTTP timeout in milliseconds
    max-retries: 3      # Max retry attempts
```

### Environment Variables
```bash
SERVER_PORT=8080
LOGGING_LEVEL_COM_EXAMPLE_EXCHANGERATE=INFO
```

## Possible Improvements

### Short Term
1. **TTL-based Cache Expiration**
   - Add time-based cache invalidation
   - Configurable cache expiration per currency pair

2. **Rate Limiting**
   - Implement request rate limiting to prevent abuse
   - Use Spring's `@RateLimiter` or custom implementation

3. **Enhanced Error Handling**
   - Detailed error responses with error codes
   - Circuit breaker pattern for failing APIs

4. **API Versioning**
   - Version the REST API (`/api/v1/exchangeRates`)
   - Backward compatibility support

### Medium Term
1. **Database Integration**
   - Store historical rates in PostgreSQL/MySQL
   - Query optimization and indexing

2. **Distributed Caching**
   - Redis cluster for shared cache across instances
   - Cache warming strategies

3. **Advanced Metrics**
   - Response time percentiles (P50, P95, P99)
   - API reliability scoring
   - Prometheus/Grafana integration

4. **Configuration Management**
   - Externalized configuration with Spring Cloud Config
   - Feature toggles for API endpoints

### Long Term
1. **Microservices Architecture**
   - Separate services for different concerns
   - API Gateway for routing and authentication

2. **Event-Driven Updates**
   - Real-time rate updates via WebSocket
   - Event sourcing for audit trails

3. **Machine Learning**
   - Predictive modeling for exchange rate trends
   - Anomaly detection for unusual rate movements

4. **Multi-Region Deployment**
   - Geographic distribution for lower latency
   - Data consistency across regions

## Troubleshooting

### Common Issues

#### Application Won't Start
```bash
# Check Java version
java -version

# Check if port is available
netstat -tlnp | grep 8080

# Check application logs
tail -f logs/application.log
```

#### External API Failures
```bash
# Test API connectivity
curl "https://api.frankfurter.app/v1/latest?base=EUR&symbols=USD"
curl "https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@latest/v1/currencies/eur.json"

# Check metrics endpoint for error counts
curl "http://localhost:8080/api/metrics"
```

#### Cache Issues
- Restart application to clear cache
- Monitor cache hit/miss ratios in logs
- Consider cache size limits if memory usage is high

### Performance Tuning

#### JVM Options
```bash
java -Xms512m -Xmx1024m -XX:+UseG1GC -jar exchange-rate-service.jar
```

#### Connection Pool Tuning
```yaml
spring:
  webflux:
    client:
      pool:
        max-connections: 100
        max-life-time: 30s
```

## Contributing

### Development Setup
1. Fork the repository
2. Create a feature branch: `git checkout -b feature/new-feature`
3. Make changes and add tests
4. Ensure all tests pass: `mvn test`
5. Submit a pull request

### Code Style
- Follow Java naming conventions
- Use SpringBoot best practices
- Write tests for new features
- Update documentation

### Testing Guidelines
- Unit tests for business logic
- Integration tests for API endpoints
- Mock external dependencies
- Aim for 80%+ test coverage

## Security Considerations

### Current Implementation
- No authentication (suitable for internal use)
- Input validation on API parameters
- No sensitive data in logs

### Production Recommendations
1. **Authentication & Authorization**
   - API keys or OAuth 2.0
   - Rate limiting per user/API key

2. **Input Validation**
   - Strict currency code validation
   - Request size limits

3. **Security Headers**
   - HTTPS enforcement
   - CORS configuration
   - Security headers (HSTS, CSP)

4. **Monitoring & Alerting**
   - Log security events
   - Monitor for unusual patterns
   - Automated alerting for failures

## License

MIT License - see LICENSE file for details

## Support

For questions or issues:
- Create an issue in the GitHub repository
- Check the troubleshooting section above
- Review the application logs for error details

---

## SpringBoot Learning Notes

This project demonstrates several key SpringBoot concepts:

### Dependency Injection
```java
@Autowired
public ExchangeRateService(List<ExchangeRateApiClient> apiClients) {
    // Spring automatically injects all beans implementing ExchangeRateApiClient
}
```

### Auto-Configuration
SpringBoot automatically configures:
- Embedded Tomcat server
- Jackson JSON processing
- WebClient HTTP client
- JUnit testing framework

### Annotations Explained
- `@SpringBootApplication`: Main application class
- `@RestController`: Marks a class as a REST API controller
- `@Service`: Marks a class as a business service
- `@Configuration`: Marks a class as a configuration source
- `@Bean`: Creates a Spring-managed object
- `@Autowired`: Injects dependencies automatically

### Testing Annotations
- `@SpringBootTest`: Full application context for integration tests
- `@WebMvcTest`: Only web layer for controller tests
- `@MockBean`: Creates mock beans in Spring context
- `@ExtendWith(MockitoExtension.class)`: Enables Mockito in JUnit 5

This project serves as a practical introduction to modern Java web development with SpringBoot!