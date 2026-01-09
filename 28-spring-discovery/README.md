# Eureka Example

## Usage

_Run Service_

```
cd eureka-service
mvn spring-boot:run
```

_Run Some Client_

```
cd eureka-service-example
mvn spring-boot:run
```

Note: You can start this service multiple times on different ports to demonstrate multiple instances:
```
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8082"
```

_Run Other Client_

```
cd eureka-service-usage
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8083"
```

Open [http://localhost:8761] and see, that there are service instances registered.

## Testing the Endpoints

The `eureka-service-usage` provides three endpoints to demonstrate different service discovery approaches:

1. **`/lookup`** - Shows all discovered service instances:
   ```bash
   curl http://localhost:8083/lookup
   ```

2. **`/hello`** - Manually discovers and calls the first instance (not load-balanced):
   ```bash
   curl -v http://localhost:8083/hello
   ```
   This demonstrates manual service discovery but always calls the same (first) instance.

   Returns clean JSON response with metadata in custom headers:
   - `X-Called-Service`: The actual URL that was called
   - `X-Discovery-Method`: `manual-first-instance`

3. **`/hello-balanced`** - Uses Spring Cloud LoadBalancer for automatic load balancing:
   ```bash
   curl -v http://localhost:8083/hello-balanced
   ```
   This is the recommended production approach - calls are automatically distributed across all available instances using round-robin.

   Returns clean JSON response with metadata in custom headers:
   - `X-Called-Service`: The service name used
   - `X-Discovery-Method`: `load-balanced`

## Note on Service Discovery Delays

When calling the `/hello` endpoint on `eureka-service-usage`, you might initially see empty results `[]` or incomplete service lists, even though all services are visible in the Eureka dashboard. This is normal behavior due to:

- **Client-side caching**: Eureka clients refresh their service registry every 30 seconds by default
- **Server-side caching**: The Eureka server's response cache updates every 30 seconds
- **Heartbeat intervals**: Services send heartbeats every 30 seconds

After waiting 30-90 seconds, the discovery client will have the complete and up-to-date service registry.