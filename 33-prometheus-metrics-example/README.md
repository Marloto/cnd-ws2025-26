# Prometheus Metrics Example

This example demonstrates **application and container metrics monitoring** using the Prometheus stack with Docker Compose.

## Stack Components

```
Spring Boot App → Micrometer → /actuator/prometheus → Prometheus → Grafana
Docker Containers → cAdvisor → Prometheus → Grafana
```

- **Prometheus**: Time-series database for metrics (pull-based)
- **cAdvisor**: Container Advisor - collects container resource metrics (CPU, memory, network)
- **Grafana**: Visualization and dashboards
- **Micrometer**: Metrics facade in Spring Boot (exposes metrics in Prometheus format)

## Architecture

```
┌─────────────────┐
│  Demo Service   │
│   (port 8081)   │
│  /actuator/     │
│   prometheus    │
└────────┬────────┘
         │
    ┌────┴──────────────┐
    │                   │
┌───▼────────┐   ┌─────▼─────┐
│  cAdvisor  │   │Prometheus │ (scrapes every 15s)
│ (port 8080)│   │(port 9090)│
└────────────┘   └─────┬─────┘
                       │
                 ┌─────▼─────┐
                 │  Grafana  │
                 │(port 3000)│
                 └───────────┘
```

## What Gets Monitored

### Application Metrics (from Spring Boot Actuator)
- **Custom metrics** defined in code:
  - `demo_requests_total` - Total HTTP requests
  - `demo_errors_total` - Total errors
  - `demo_response_time` - Response time distribution
  - `demo_active_users` - Current active users (gauge)

- **Built-in JVM metrics**:
  - Memory usage (heap, non-heap)
  - Garbage collection
  - Thread count
  - CPU usage

- **HTTP metrics**:
  - Request count by endpoint
  - Response times
  - HTTP status codes

### Container Metrics (from cAdvisor)
- CPU usage per container
- Memory usage per container
- Network I/O
- Disk I/O
- Container filesystem usage

## Quick Start

### 1. Start the Stack

```bash
docker-compose up --build
```

This starts:
- Prometheus (port 9090)
- cAdvisor (port 8080)
- Grafana (port 3000)
- Demo Service (port 8081)

### 2. Generate Some Metrics

```bash
# Normal requests
for i in {1..10}; do curl http://localhost:8081/hello; done

# Slow requests (500ms - 2s response time)
for i in {1..5}; do curl http://localhost:8081/slow; done

# Simulate user activity
curl http://localhost:8081/users/login
curl http://localhost:8081/users/login
curl http://localhost:8081/users/login
curl http://localhost:8081/users/logout

# Trigger errors
curl http://localhost:8081/error
```

### 3. View Metrics

#### Raw Metrics (Prometheus Format)
```bash
# Application metrics endpoint
curl http://localhost:8081/actuator/prometheus

# Example output:
# demo_requests_total{service="demo-service"} 42.0
# demo_active_users 3.0
# jvm_memory_used_bytes{area="heap"} 123456789
```

#### Prometheus UI
Open: http://localhost:9090

**Example PromQL queries:**
```promql
# Request rate (requests per second)
rate(demo_requests_total[1m])

# Average response time
demo_response_time_sum / demo_response_time_count

# Current active users
demo_active_users

# Error rate
rate(demo_errors_total[1m])

# JVM heap memory used
jvm_memory_used_bytes{area="heap", application="demo-service"}

# Container CPU usage (from cAdvisor)
rate(container_cpu_usage_seconds_total{name="demo-service"}[1m])

# Container memory usage
container_memory_usage_bytes{name="demo-service"}
```

#### Grafana Dashboard
Open: http://localhost:3000

Pre-configured dashboard shows:
- Request rate over time
- Total requests counter
- Average response time
- Error count
- Active users gauge
- JVM memory usage

**Manual exploration:**
1. Click "Explore" (compass icon)
2. Select "Prometheus" datasource
3. Enter PromQL query
4. Click "Run query"

#### cAdvisor UI
Open: http://localhost:8080

Shows real-time container metrics for all running containers.

## How It Works

### 1. Spring Boot Exposes Metrics

**pom.xml:**
```xml
<!-- Actuator for /actuator endpoints -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>

<!-- Prometheus format for metrics -->
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

**application.yml:**
```yaml
management:
  endpoints:
    web:
      exposure:
        include: prometheus  # Expose /actuator/prometheus
```

### 2. Custom Metrics in Code

```java
@RestController
public class DemoController {
    private final Counter requestCounter;
    private final Timer responseTimer;

    public DemoController(MeterRegistry registry) {
        // Create custom counter
        this.requestCounter = Counter.builder("demo_requests_total")
            .description("Total requests")
            .tag("service", "demo-service")
            .register(registry);

        // Create custom timer
        this.responseTimer = Timer.builder("demo_response_time")
            .description("Response time")
            .register(registry);
    }

    @GetMapping("/hello")
    public Map<String, String> hello() {
        return responseTimer.record(() -> {
            requestCounter.increment();  // Increment counter
            // ... handle request
        });
    }
}
```

### 3. Prometheus Scrapes Metrics

**prometheus.yml:**
```yaml
scrape_configs:
  # Scrape Spring Boot app
  - job_name: 'demo-service'
    metrics_path: '/actuator/prometheus'  # Where to get metrics
    static_configs:
      - targets: ['demo-service:8081']    # Service to scrape
    scrape_interval: 15s                  # How often

  # Scrape cAdvisor
  - job_name: 'cadvisor'
    static_configs:
      - targets: ['cadvisor:8080']
```

Prometheus:
1. **Discovers targets** (demo-service, cadvisor)
2. **Scrapes metrics** every 15 seconds via HTTP GET
3. **Stores time-series data** with timestamps
4. **Indexes by labels** (service, instance, etc.)

### 4. Grafana Queries and Visualizes

Grafana:
1. Connects to Prometheus datasource
2. Runs PromQL queries
3. Renders graphs/charts
4. Auto-refreshes dashboards

## Metric Types

### Counter
Monotonically increasing value (never decreases)
```java
Counter.builder("demo_requests_total").register(registry);
// Usage: counter.increment();
```
**Use for:** Total requests, errors, operations

### Gauge
Current value that can go up or down
```java
registry.gauge("demo_active_users", activeUsers);
```
**Use for:** Active users, queue size, temperature

### Timer
Measures duration and provides distribution
```java
Timer timer = Timer.builder("demo_response_time").register(registry);
timer.record(() -> { /* operation */ });
```
**Use for:** Response times, operation durations

### Histogram
Samples observations and counts them in buckets
```java
DistributionSummary.builder("order_size").register(registry);
```
**Use for:** Request sizes, payload sizes

## PromQL Examples

### Rate Calculations
```promql
# Requests per second (averaged over 1 minute)
rate(demo_requests_total[1m])

# Errors per second
rate(demo_errors_total[5m])
```

### Aggregations
```promql
# Total requests across all instances
sum(demo_requests_total)

# Average response time
avg(demo_response_time_sum / demo_response_time_count)
```

### Percentiles (from Timer)
```promql
# 95th percentile response time
histogram_quantile(0.95, rate(demo_response_time_bucket[5m]))

# 99th percentile
histogram_quantile(0.99, rate(demo_response_time_bucket[5m]))
```

### Filtering by Labels
```promql
# Only errors from demo-service
demo_errors_total{service="demo-service"}

# Memory usage for specific container
container_memory_usage_bytes{name="demo-service"}
```

### Mathematical Operations
```promql
# Error rate as percentage
(rate(demo_errors_total[1m]) / rate(demo_requests_total[1m])) * 100

# Memory usage in MB
container_memory_usage_bytes / 1024 / 1024
```

## Comparison: Prometheus vs Alternatives

| Feature | Prometheus | InfluxDB | Graphite |
|---------|------------|----------|----------|
| **Architecture** | Pull (scrape) | Push | Push |
| **Query Language** | PromQL | InfluxQL | Graphite functions |
| **Data Model** | Multi-dimensional labels | Tags + fields | Hierarchical paths |
| **Service Discovery** | Built-in | Manual | Manual |
| **Alerting** | AlertManager | Kapacitor | External |
| **K8s Integration** | Native | Manual | Manual |
| **Best For** | Monitoring, alerting | IoT, analytics | Legacy systems |

## Pull vs Push Model

### Prometheus (Pull)
```
Prometheus → HTTP GET → /actuator/prometheus → Service
           ← Metrics
```
**Advantages:**
- Centralized control (Prometheus decides when to scrape)
- Easier to detect if target is down
- No need to configure push destinations in apps

### StatsD/Graphite (Push)
```
Service → UDP → StatsD → Graphite
```
**Disadvantages:**
- Apps must know where to push
- UDP packets can be lost
- Harder to debug

## Production Considerations

### Data Retention
```yaml
# prometheus.yml
command:
  - '--storage.tsdb.retention.time=15d'  # Keep 15 days of data
```

### Scaling Prometheus
For high metric volume:
1. **Federation**: Multiple Prometheus instances, one aggregates
2. **Remote storage**: Long-term storage in external systems
3. **Thanos/Cortex**: Distributed Prometheus solutions

### Security
In production:
- Enable authentication on Grafana
- Use TLS for Prometheus endpoints
- Restrict /actuator endpoints to internal network

### Alert Manager
Add alerting rules:
```yaml
# alert.rules.yml
groups:
  - name: demo-alerts
    rules:
      - alert: HighErrorRate
        expr: rate(demo_errors_total[5m]) > 0.1
        annotations:
          summary: "High error rate detected"
```

## cAdvisor Metrics

Key container metrics from cAdvisor:

```promql
# CPU usage
rate(container_cpu_usage_seconds_total[1m])

# Memory usage
container_memory_usage_bytes

# Network received bytes
rate(container_network_receive_bytes_total[1m])

# Network transmitted bytes
rate(container_network_transmit_bytes_total[1m])

# Filesystem usage
container_fs_usage_bytes
```

## Stopping the Stack

```bash
# Stop all services
docker-compose down

# Stop and remove volumes (deletes metrics data)
docker-compose down -v
```

## Observability Stack

This metrics example complements:
- **Logs**: 32-loki-logging-example (Loki + Promtail + Grafana)
- **Metrics**: 33-prometheus-metrics-example (Prometheus + cAdvisor + Grafana)
- **Traces** (optional): Tempo + OpenTelemetry

Together they provide complete observability:
```
Logs    → "What happened?" (events, errors)
Metrics → "How much/many?" (performance, resources)
Traces  → "Where did it go?" (request flow)
```

## Next Steps

1. **Create custom dashboards** in Grafana for specific use cases
2. **Add AlertManager** for notifications (email, Slack, PagerDuty)
3. **Integrate with Loki** for unified logs + metrics view
4. **Add more services** to see multi-service metrics
5. **Set up recording rules** for pre-computed aggregations

## References

- [Prometheus Documentation](https://prometheus.io/docs/)
- [PromQL Basics](https://prometheus.io/docs/prometheus/latest/querying/basics/)
- [Micrometer Documentation](https://micrometer.io/docs)
- [cAdvisor GitHub](https://github.com/google/cadvisor)
- [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)

## Key Takeaways

1. **Pull-based**: Prometheus scrapes metrics (vs push-based alternatives)
2. **Labels are powerful**: Multi-dimensional querying (like Loki)
3. **Metric types matter**: Counter, Gauge, Timer, Histogram have different uses
4. **Two-layer monitoring**: Application (Actuator) + Infrastructure (cAdvisor)
5. **PromQL is essential**: Learn the query language for effective monitoring
6. **Grafana dashboards**: Pre-built dashboards save time

## Comparison Points for Teaching

**Why Prometheus?**
- Industry standard for cloud-native monitoring
- Native Kubernetes integration
- Powerful query language (PromQL)
- Active community and ecosystem
- Works well with dynamic environments

**Prometheus vs Logging:**
- Metrics are aggregated numbers (cheaper to store)
- Logs are individual events (more detail)
- Use both together for complete picture
