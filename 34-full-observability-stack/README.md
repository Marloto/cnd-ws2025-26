# Full Observability Stack - Complete Spring Cloud Microservices Example

This is the **ultimate all-in-one example** combining:
- **Spring Cloud** (Config, Discovery, Gateway)
- **Logging** (Loki + Promtail)
- **Metrics** (Prometheus + cAdvisor)
- **Visualization** (Grafana)

All running in Docker with complete observability for learning microservice infrastructure.

## Architecture Overview

```
┌────────────────────────────────────────────────────────────┐
│                    Spring Cloud Layer                      │
│  ┌──────────┐  ┌──────────┐  ┌─────────┐                   │
│  │  Config  │  │  Eureka  │  │ Gateway │                   │
│  │  Server  │  │  Server  │  │         │                   │
│  │  :8888   │  │  :8761   │  │  :8080  │                   │
│  └────┬─────┘  └────┬─────┘  └────┬────┘                   │
│       │             │              │                       │
│       └─────────────┴──────────────┴───────┬────────┐      │
│                                            │        │      │
│                                      ┌─────▼──┐  ┌──▼────┐ │
│                                      │Service │  │Service│ │
│                                      │   A    │  │   B   │ │
│                                      │  :8081 │  │ :8082 │ │
│                                      └────┬───┘  └───┬───┘ │
└───────────────────────────────────────────┼──────────┼─────┘
                                            │          │
                    ┌───────────────────────┴──────────┴──────┐
                    │    Logs (JSON to stdout)                │
                    │    Metrics (/actuator/prometheus)       │
                    └───────────────┬─────────────────────────┘
                                    │
        ┌───────────────────────────┼───────────────────────────┐
        │                           │                           │
   ┌────▼─────┐              ┌──────▼────┐             ┌───────▼────┐
   │ Promtail │              │Prometheus │             │  cAdvisor  │
   │(collects │              │ (scrapes) │             │(container  │
   │  logs)   │              │           │             │  metrics)  │
   └────┬─────┘              └──────┬────┘             └───────┬────┘
        │                           │                          │
   ┌────▼─────┐              ┌──────▼──────────────────────────▼────┐
   │   Loki   │              │          Prometheus                  │
   │  :3100   │              │            :9090                     │
   └────┬─────┘              └──────┬───────────────────────────────┘
        │                           │
        └───────────────┬───────────┘
                        │
                 ┌──────▼──────┐
                 │   Grafana   │
                 │    :3000    │
                 │ (Logs +     │
                 │  Metrics)   │
                 └─────────────┘
```

## What's Included

### Spring Cloud Infrastructure (5 services)
1. **config-server** (port 8888): Centralized configuration
2. **eureka-server** (port 8761): Service discovery
3. **gateway** (port 8080): API Gateway with routing
4. **service-a** (port 8081): Example microservice
5. **service-b** (port 8082): Example microservice with service-to-service calls

### Observability Stack (6 components)
6. **Loki** (port 3100): Log aggregation
7. **Promtail**: Log collector (reads Docker logs)
8. **Prometheus** (port 9090): Metrics time-series database
9. **cAdvisor** (port 8180): Container resource metrics
10. **Grafana** (port 3000): Unified visualization for logs & metrics

## Quick Start

### Prerequisites
- Docker & Docker Compose
- 8GB+ RAM recommended
- Ports: 3000, 3100, 8080, 8081, 8082, 8761, 8888, 9090

### 1. Start Everything

```bash
docker-compose up --build
```

**First startup takes 5-10 minutes** due to:
- Building 5 Spring Boot services (Maven downloads dependencies)
- Waiting for health checks
- Service registration with Eureka

**Startup sequence:**
1. Config Server (40s)
2. Eureka Server (40s)
3. Gateway, Service A, Service B (30s each)
4. Loki, Prometheus, Grafana (instant)

**Watch logs:**
```bash
docker-compose logs -f
```

### 2. Verify Services are Ready

Check Eureka Dashboard: http://localhost:8761

You should see:
- GATEWAY
- SERVICE-A
- SERVICE-B

### 3. Test the Application

```bash
# Via Gateway → Service A
curl http://localhost:8080/service-a/hello

# Via Gateway → Service B
curl http://localhost:8080/service-b/hello

# Service B calls Service A (inter-service communication)
curl http://localhost:8080/service-b/call-service-a
```

Expected response:
```json
{
  "service": "service-b",
  "message": "Hello from Service B",
  "port": "8082",
  "called-service": "service-a",
  "service-a-response": {
    "service": "service-a",
    "message": "Hello from Service A",
    "port": "8081"
  }
}
```

### 4. Generate Load for Observability

```bash
# Generate traffic
for i in {1..20}; do
  curl http://localhost:8080/service-a/hello
  curl http://localhost:8080/service-b/call-service-a
done
```

## Observability Dashboards

### Grafana - Unified Logs & Metrics
**URL:** http://localhost:3000

**Pre-configured datasources:**
- Loki (logs)
- Prometheus (metrics)

**View Logs:**
1. Click "Explore" → Select "Loki"
2. Query: `{service="service-a"}`
3. See JSON logs with trace IDs

**View Metrics:**
1. Click "Explore" → Select "Prometheus"
2. Query: `rate(http_server_requests_seconds_count[1m])`
3. See request rates per service

### Prometheus - Raw Metrics
**URL:** http://localhost:9090

**Example PromQL queries:**
```promql
# HTTP request rate per service
rate(http_server_requests_seconds_count{application="service-a"}[1m])

# JVM memory usage
jvm_memory_used_bytes{application="service-a", area="heap"}

# Container CPU usage
rate(container_cpu_usage_seconds_total{name=~".*service-a.*"}[1m])

# Container memory usage
container_memory_usage_bytes{name=~".*service-a.*"}
```

### Loki - Raw Logs
**URL:** http://localhost:3100 (API only, no UI)

Use Grafana Explore with Loki datasource.

**Example LogQL queries:**
```logql
# All logs from service-a
{service="service-a"}

# Error logs only
{level="ERROR"}

# Logs with specific trace ID (for distributed tracing)
{trace_id="abc-123"}

# Service B calling Service A
{service="service-b"} |= "calling Service A"
```

### Eureka Dashboard - Service Discovery
**URL:** http://localhost:8761

Shows registered services and health status.

### cAdvisor - Container Stats
**URL:** http://localhost:8180

Real-time container resource usage.

## Observability Features

### 1. Distributed Logging (Loki)

**How it works:**
```
Service → stdout (JSON) → Docker logs → Promtail → Loki → Grafana
```

**Features:**
- **Structured JSON logs** with timestamp, level, logger, message
- **Trace IDs** for distributed tracing across services
- **Automatic collection** via Docker labels
- **No application changes** needed (just stdout)

**Log format:**
```json
{
  "timestamp": "2026-01-08T15:30:00.123+0000",
  "level": "INFO",
  "logger_name": "de.thi.inf.cnd.servicea.HelloController",
  "message": "Processing hello request",
  "application": "service-a",
  "traceId": "abc-123",
  "spanId": "def-456"
}
```

### 2. Application Metrics (Prometheus)

**How it works:**
```
Service → /actuator/prometheus → Prometheus (scrapes every 15s) → Grafana
```

**Metrics exposed:**
- **JVM metrics**: Memory, GC, threads, CPU
- **HTTP metrics**: Request count, duration, status codes
- **Custom metrics**: Business metrics (if added)

**Endpoint:**
```bash
curl http://localhost:8081/actuator/prometheus
```

### 3. Container Metrics (cAdvisor)

**Collects:**
- CPU usage per container
- Memory usage per container
- Network I/O
- Disk I/O

**Query in Prometheus:**
```promql
# Service A CPU usage
rate(container_cpu_usage_seconds_total{name=~".*service-a.*"}[1m]) * 100

# Service A memory
container_memory_usage_bytes{name=~".*service-a.*"} / 1024 / 1024
```

## Complete Observability Workflow

### Scenario: Debug a Slow Request

1. **User reports:** "Service B is slow"

2. **Check metrics in Grafana:**
```promql
# Response time for service-b
histogram_quantile(0.95, rate(http_server_requests_seconds_bucket{application="service-b"}[5m]))
```
**Finding:** Service B's 95th percentile response time is 2s

3. **Check logs for errors:**
```logql
{service="service-b", level="ERROR"}
```
**Finding:** No errors, but many "Calling Service A" messages

4. **Check if Service A is slow:**
```promql
histogram_quantile(0.95, rate(http_server_requests_seconds_bucket{application="service-a"}[5m]))
```
**Finding:** Service A is responding in 100ms (fast)

5. **Check network latency between containers:**
```promql
rate(container_network_receive_bytes_total{name=~".*service-b.*"}[1m])
```

6. **Find specific slow request with trace ID:**
```logql
{service="service-b"} | json | duration > 1
```
Copy trace ID from log

7. **See full request flow across both services:**
```logql
{trace_id="xyz-789"}
```
**Result:** See logs from both Service A and Service B for that request

## Configuration

### Spring Cloud Config First

All services get configuration from Config Server:

```yaml
spring:
  config:
    import: optional:configserver:http://config-server:8888
```

**Config files needed in Git repository:**

Use the same files from example 31, but with environment variable placeholders for Docker compatibility:

**gateway.yml, service-a.yml, service-b.yml:**
```yaml
eureka:
  client:
    service-url:
      defaultZone: ${EUREKA_URL:http://localhost:8761/eureka/}
```

This works for both:
- **Local (example 31)**: Uses default `http://localhost:8761/eureka/`
- **Docker (example 34)**: Overridden via environment variable to `http://eureka-server:8761/eureka/`

The docker-compose.yml sets `EUREKA_URL=http://eureka-server:8761/eureka/` for all services.

See the original [31-spring-cloud-combined README](../31-spring-cloud-combined/README.md) for complete Git config file examples.

### Service Discovery

Services register with Eureka:
- Eureka Server: http://localhost:8761
- Services auto-register on startup
- Gateway discovers services via Eureka

### Logging Configuration

All services use **logback-spring.xml**:
```xml
<encoder class="net.logstash.logback.encoder.LogstashEncoder">
    <customFields>{"application":"service-name"}</customFields>
</encoder>
```

### Metrics Configuration

All services expose Prometheus metrics via **application.yml**:
```yaml
management:
  endpoints:
    web:
      exposure:
        include: prometheus
  metrics:
    export:
      prometheus:
        enabled: true
```

## Scaling Services

Run multiple instances:

```bash
# Scale Service A to 3 instances
docker-compose up --scale service-a=3

# Gateway will load-balance across all 3
for i in {1..10}; do curl http://localhost:8080/service-a/hello; done
```

**Observe in:**
- **Eureka:** 3 instances of SERVICE-A
- **Prometheus:** Metrics from all 3 instances
- **Loki:** Logs from all 3 containers (tagged by container name)

## Stopping the Stack

```bash
# Stop all services
docker-compose down

# Stop and remove volumes (deletes logs & metrics data)
docker-compose down -v
```

## Component Port Summary

| Component | Port | Purpose |
|-----------|------|---------|
| **config-server** | 8888 | Spring Cloud Config |
| **eureka-server** | 8761 | Service Discovery |
| **gateway** | 8080 | API Gateway |
| **service-a** | 8081 | Microservice A |
| **service-b** | 8082 | Microservice B |
| **loki** | 3100 | Log API |
| **prometheus** | 9090 | Metrics API & UI |
| **cAdvisor** | 8180 | Container Metrics UI |
| **grafana** | 3000 | Unified Dashboard |

## Observability Stack Comparison

### Logs vs Metrics vs Traces

| Aspect | Logs (Loki) | Metrics (Prometheus) | Traces (not included) |
|--------|-------------|----------------------|----------------------|
| **Question** | What happened? | How much/many? | Where did it go? |
| **Format** | Events, text | Numbers, aggregates | Request flow |
| **Example** | "User login failed" | "95% of requests < 200ms" | "Request through 5 services" |
| **Volume** | High | Low | Medium |
| **Storage** | Text | Time-series | Spans |

This example includes **Logs + Metrics**. For traces, add **Tempo** or **Jaeger**.

### vs Kubernetes

This Docker-based example shows concepts that translate to Kubernetes:

| This Example | Kubernetes Equivalent |
|--------------|----------------------|
| Eureka (Discovery) | K8s Services + DNS |
| Config Server | ConfigMaps + Secrets |
| Gateway | Ingress Controller |
| Promtail | Fluent Bit / Promtail |
| Loki | Loki (same) |
| Prometheus | Prometheus (same) |
| cAdvisor | Node Exporter + cAdvisor |
| Grafana | Grafana (same) |

**Why learn both?**
- This example: Understand the concepts
- Kubernetes: How it's done in production

## Troubleshooting

### Services not starting

**Check health:**
```bash
docker-compose ps
```

**Common issues:**
1. **Config Server not ready:** Wait 40s for health check
2. **Port conflicts:** Ensure ports 3000, 8080, 8081, 8082, 8761, 8888, 9090 are free
3. **Out of memory:** Increase Docker memory to 8GB+

### Services not registering with Eureka

**Wait 60 seconds** - Eureka has cache delays:
- 30s for service to register
- 30s for clients to fetch registry

**Check logs:**
```bash
docker-compose logs service-a | grep -i eureka
```

### No logs in Grafana

**Check Promtail is collecting:**
```bash
docker-compose logs promtail
```

**Verify Docker labels:**
```bash
docker inspect 34-full-observability-stack_service-a_1 | grep -A5 Labels
```

Should see: `"logging": "promtail"`

### No metrics in Prometheus

**Check metrics endpoint:**
```bash
curl http://localhost:8081/actuator/prometheus
```

**Check Prometheus targets:**
http://localhost:9090/targets

All targets should be "UP".

## References

- [Spring Cloud Documentation](https://spring.io/projects/spring-cloud)
- [Prometheus Documentation](https://prometheus.io/docs/)
- [Grafana Loki Documentation](https://grafana.com/docs/loki/)
- [Micrometer Documentation](https://micrometer.io/)
- [Docker Compose Documentation](https://docs.docker.com/compose/)

