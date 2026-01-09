# Loki Logging Stack with Docker Compose

This example demonstrates centralized logging for microservices using the **Grafana Loki stack** with Docker Compose.

## Stack Components

```
Spring Boot Services → stdout (JSON) → Docker logs → Promtail → Loki → Grafana
```

- **Loki**: Log aggregation system (like Elasticsearch, but lighter)
- **Promtail**: Log collector (reads Docker container logs)
- **Grafana**: Visualization and querying UI

## Architecture

```
┌─────────────┐    ┌─────────────┐
│  Service A  │    │  Service B  │
│  (port 8081)│    │  (port 8082)│
└──────┬──────┘    └──────┬──────┘
       │ JSON logs        │ JSON logs
       └────────┬─────────┘
                │
         Docker stdout
                │
          ┌─────▼─────┐
          │ Promtail  │ (reads Docker logs)
          └─────┬─────┘
                │
          ┌─────▼─────┐
          │   Loki    │ (stores logs)
          └─────┬─────┘
                │
          ┌─────▼─────┐
          │  Grafana  │ (visualizes)
          │  :3000    │
          └───────────┘
```

## Quick Start

### 1. Start the Stack

```bash
docker-compose up -d
```

This starts:
- Loki (port 3100)
- Promtail (collects logs automatically)
- Grafana (port 3000)
- Service A (port 8081)
- Service B (port 8082)

### 2. Access Grafana

Open browser: http://localhost:3000

Grafana is pre-configured with:
- Anonymous access enabled (no login needed)
- Loki datasource already added

### 3. Generate Some Logs

```bash
# Call Service A
curl http://localhost:8081/hello

# Call Service B
curl http://localhost:8082/hello

# Service B calls Service A (generates correlated logs)
curl http://localhost:8082/call-service-a
```

### 4. View Logs in Grafana

1. Go to http://localhost:3000
2. Click "Explore" (compass icon on left sidebar)
3. Select "Loki" datasource (should be default)

**Example LogQL queries:**

```logql
# All logs from service-a
{service="service-a"}

# Only ERROR level logs
{service="service-a"} |= "ERROR"

# Logs with specific trace ID
{trace_id="abc123"}

# All logs from both services
{service=~"service-.*"}

# Logs containing "hello"
{service="service-a"} |= "hello"

# Filter by log level label
{level="ERROR"}

# Count errors per service
sum by (service) (count_over_time({level="ERROR"}[5m]))
```

## Log Format

Spring Boot services output JSON logs:

```json
{
  "timestamp": "2026-01-08T10:30:00.123Z",
  "level": "INFO",
  "logger_name": "de.thi.inf.cnd.servicea.HelloController",
  "message": "Processing hello request",
  "thread_name": "http-nio-8081-exec-1",
  "traceId": "abc123",
  "spanId": "def456"
}
```

Promtail extracts fields and sends to Loki as labels:
- `service`: Service name (service-a, service-b)
- `container`: Docker container name
- `level`: Log level (INFO, WARN, ERROR)
- `trace_id`: Correlation ID (if present)
- `span_id`: Span ID (if present)

## How It Works

### 1. Spring Boot Configuration

Services use `logstash-logback-encoder` to output JSON:

**pom.xml:**
```xml
<dependency>
    <groupId>net.logstash.logback</groupId>
    <artifactId>logstash-logback-encoder</artifactId>
    <version>7.4</version>
</dependency>
```

**logback-spring.xml:**
```xml
<configuration>
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder class="net.logstash.logback.encoder.LogstashEncoder"/>
    </appender>
    <root level="INFO">
        <appender-ref ref="STDOUT"/>
    </root>
</configuration>
```

### 2. Docker Labels

Services have labels for Promtail filtering:

```yaml
labels:
  logging: "promtail"    # Promtail only collects containers with this label
  service: "service-a"   # Used as 'service' label in Loki
```

### 3. Promtail Collection

Promtail:
1. Discovers Docker containers with `logging=promtail` label
2. Reads logs from `/var/lib/docker/containers`
3. Parses JSON format
4. Extracts labels (service, level, trace_id, etc.)
5. Pushes to Loki

### 4. Loki Storage

Loki:
- Indexes labels (service, level, trace_id)
- Stores log content (not indexed for cost savings)
- Enables fast filtering by labels

### 5. Grafana Querying

Use LogQL (similar to PromQL) to query logs:
- Filter by labels: `{service="service-a"}`
- Text search: `|= "error"`
- JSON field extraction: `| json | level="ERROR"`

## Trace Correlation Example

When Service B calls Service A, both share the same trace ID:

```bash
curl http://localhost:8082/call-service-a
```

**Service B log:**
```json
{
  "service": "service-b",
  "message": "Calling service-a",
  "traceId": "xyz789"
}
```

**Service A log:**
```json
{
  "service": "service-a",
  "message": "Processing hello request",
  "traceId": "xyz789"
}
```

**Query in Grafana:**
```logql
{trace_id="xyz789"}
```

This returns all logs from both services for that request!

## Resource Usage

Typical memory usage:
- Loki: ~50-100 MB
- Promtail: ~20-30 MB
- Grafana: ~100-150 MB

**Total: ~200-300 MB** (vs. ELK stack: 2-4 GB)

## Production Considerations

### Data Retention

Default: Logs kept forever (or until disk full)

Configure in Loki:
```yaml
# Add to loki config
limits_config:
  retention_period: 744h  # 31 days
```

### Scaling

For high log volume:
1. Use **Loki in microservices mode** (separate read/write/backend)
2. Add **object storage** (S3, GCS) for long-term storage
3. Run **multiple Promtail instances**

### Security

For production:
1. Enable Grafana authentication (remove anonymous access)
2. Use TLS for Loki API
3. Implement multi-tenancy in Loki

## Comparison: Loki vs ELK

| Feature | Loki | ELK |
|---------|------|-----|
| **Setup** | Simple (3 containers) | Complex (5+ containers) |
| **Memory** | ~200 MB | ~2-4 GB |
| **Indexing** | Labels only | Full-text |
| **Search** | Fast for labels | Fast for everything |
| **Cost** | Very low | High |
| **Best for** | K8s, Docker, cost-sensitive | Complex analytics, compliance |

## Stopping the Stack

```bash
# Stop all services
docker-compose down

# Stop and remove volumes (deletes logs)
docker-compose down -v
```

## Troubleshooting

### Logs not appearing in Grafana

1. Check Promtail is running:
```bash
docker-compose logs promtail
```

2. Check Promtail can reach Loki:
```bash
docker-compose exec promtail wget -O- http://loki:3100/ready
```

3. Check Docker labels on services:
```bash
docker inspect service-a | grep -A5 Labels
```

### Loki not starting

Check disk space:
```bash
df -h
```

Loki needs writable `/loki` directory.

## Next Steps

1. **Add distributed tracing**: Integrate with Tempo (traces) and Prometheus (metrics) for full observability
2. **Custom dashboards**: Create Grafana dashboards for specific services
3. **Alerting**: Set up alerts for ERROR level logs
4. **Multi-tenancy**: Separate logs by team/environment

## References

- [Loki Documentation](https://grafana.com/docs/loki/latest/)
- [LogQL Query Language](https://grafana.com/docs/loki/latest/logql/)
- [Promtail Configuration](https://grafana.com/docs/loki/latest/clients/promtail/configuration/)
