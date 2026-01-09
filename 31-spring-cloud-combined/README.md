# Spring Cloud Combined Example - Config First Architecture

This example demonstrates a complete Spring Cloud microservices setup using the **Config First** approach, where all services discover their configuration from Spring Cloud Config Server before registering with Eureka.

## Architecture Overview

```
┌─────────────────┐
│  Config Server  │ (Port 8888) - Fixed URL, provides configuration
└────────┬────────┘
         │
    ┌────┴────┬────────┬────────┐
    │         │        │        │
┌───▼────┐ ┌─▼────┐ ┌─▼─────┐ ┌▼──────┐
│ Eureka │ │ Gate │ │ Srv-A │ │ Srv-B │
│ Server │ │ way  │ │       │ │       │
└───┬────┘ └─┬────┘ └─┬─────┘ └┬──────┘
    │        │        │        │
    └────────┴────────┴────────┘
    All register with Eureka
```

## Components

1. **config-server** (port 8888): Centralized configuration server
2. **eureka-server** (port 8761): Service discovery server
3. **gateway** (port 8080): API Gateway with routing
4. **service-a** (port 8081): Example microservice A
5. **service-b** (port 8082): Example microservice B

## Prerequisites

- Java 21
- Maven 3.x
- Git repository with configuration files (see below)

## Git Configuration Repository Setup

You need a Git repository containing configuration files for all services. Create the following files in your Git repository (e.g., `https://github.com/Marloto/spring-boot-config-example.git`):

### Required Configuration Files

#### `eureka-server.yml`
```yaml
server:
  port: 8761

eureka:
  client:
    register-with-eureka: false
    fetch-registry: false
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

#### `gateway.yml`
```yaml
server:
  port: 8080

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/

spring:
  cloud:
    gateway:
      discovery:
        locator:
          enabled: true
          lower-case-service-id: true
      routes:
        - id: service-a
          uri: lb://service-a
          predicates:
            - Path=/service-a/**
          filters:
            - StripPrefix=1
        - id: service-b
          uri: lb://service-b
          predicates:
            - Path=/service-b/**
          filters:
            - StripPrefix=1
```

#### `service-a.yml`
```yaml
server:
  port: 8081

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/

service:
  message: Hello from Service A
```

#### `service-b.yml`
```yaml
server:
  port: 8082

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/

service:
  message: Hello from Service B
```

### Optional: Profile-Specific Configuration

You can create profile-specific files:
- `service-a-development.yml`
- `service-a-productive.yml`
- `service-b-development.yml`
- `service-b-productive.yml`

## Startup Instructions

**Important:** Services must be started in this order due to Config First dependencies:

### 1. Start Config Server (First!)
```bash
cd config-server
mvn spring-boot:run
```
Wait until you see: `Tomcat started on port 8888`

### 2. Start Eureka Server
```bash
cd eureka-server
mvn spring-boot:run
```
Wait until you see: `Tomcat started on port 8761`

### 3. Start Gateway
```bash
cd gateway
mvn spring-boot:run
```
Wait until you see: `Tomcat started on port 8080`

### 4. Start Services (can be started in parallel)

**Terminal 1 - Service A:**
```bash
cd service-a
mvn spring-boot:run
```

**Terminal 2 - Service B:**
```bash
cd service-b
mvn spring-boot:run
```

Optional: Start multiple instances of a service:
```bash
cd service-a
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8091"
```

## Testing the Setup

### 1. Check Config Server
```bash
curl http://localhost:8888/service-a/default
curl http://localhost:8888/service-b/default
```

### 2. Check Eureka Dashboard
Open browser: http://localhost:8761

You should see registered instances:
- GATEWAY
- SERVICE-A
- SERVICE-B

### 3. Direct Service Access
```bash
# Service A
curl http://localhost:8081/hello

# Service B
curl http://localhost:8082/hello
```

Expected response:
```json
{
  "service": "service-a",
  "message": "Hello from Service A",
  "port": "8081"
}
```

### 4. Service-to-Service Communication
```bash
# Service B calls Service A
curl http://localhost:8082/call-service-a
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

This demonstrates service-to-service communication using Eureka discovery and client-side load balancing.

### 5. Gateway Routing
```bash
# Via Gateway to Service A
curl http://localhost:8080/service-a/hello

# Via Gateway to Service B
curl http://localhost:8080/service-b/hello

# Via Gateway to Service B calling Service A
curl http://localhost:8080/service-b/call-service-a
```

### 6. Load Balancing Test

Start second instance of Service A:
```bash
cd service-a
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8091"
```

Call multiple times to see load balancing:
```bash
for i in {1..10}; do curl http://localhost:8080/service-a/hello; echo; done
```

You should see responses from both port 8081 and 8091.

## Configuration Refresh

To update configuration without restarting:

1. Update the Git repository configuration file
2. Commit and push changes
3. Refresh the service:
```bash
curl -X POST http://localhost:8081/actuator/refresh
```

Note: Only works for `@RefreshScope` beans and `@ConfigurationProperties`, not `@Value`.

## Config First vs Discovery First

This example uses **Config First** approach:
- **Advantage:** Single source of truth (Config Server)
- **Disadvantage:** Config Server is a single point of failure

Alternative **Discovery First** approach:
- Config Server registers with Eureka
- Services discover Config Server via Eureka
- More resilient but adds complexity

## Common Issues

### Services fail to start with "Connection refused" to Config Server
- Ensure Config Server is running on port 8888
- Check `spring.config.import` in application.yml includes `optional:configserver:`

### Services don't appear in Eureka
- Wait 30-60 seconds (Eureka cache delay)
- Check Eureka server URL in configuration
- Verify network connectivity

### Gateway returns 503 Service Unavailable
- Verify target services are registered in Eureka
- Check gateway routes configuration
- Ensure service names match in gateway.yml and application.name

## Technology Stack

- Spring Boot 3.4.1
- Spring Cloud 2024.0.2 (Moorgate)
- Java 21
- Spring Cloud Config
- Spring Cloud Netflix Eureka
- Spring Cloud Gateway
- Spring Cloud LoadBalancer

## File Structure

```
31-spring-cloud-combined/
├── config-server/
│   ├── pom.xml
│   └── src/main/
│       ├── java/.../ConfigServerApplication.java
│       └── resources/application.yml
├── eureka-server/
│   ├── pom.xml
│   └── src/main/
│       ├── java/.../EurekaServerApplication.java
│       └── resources/application.yml
├── gateway/
│   ├── pom.xml
│   └── src/main/
│       ├── java/.../GatewayApplication.java
│       └── resources/application.yml
├── service-a/
│   ├── pom.xml
│   └── src/main/
│       ├── java/.../
│       │   ├── ServiceAApplication.java
│       │   └── HelloController.java
│       └── resources/application.yml
├── service-b/
│   ├── pom.xml
│   └── src/main/
│       ├── java/.../
│       │   ├── ServiceBApplication.java
│       │   └── HelloController.java
│       └── resources/application.yml
└── README.md
```

## Git Repository Configuration Summary

Create these files in your Git configuration repository:
- `eureka-server.yml` - Eureka server configuration
- `gateway.yml` - Gateway routes and Eureka client config
- `service-a.yml` - Service A configuration (port, message)
- `service-b.yml` - Service B configuration (port, message)

Optional profile-specific files:
- `service-a-development.yml`
- `service-a-productive.yml`
- `service-b-development.yml`
- `service-b-productive.yml`

Update `config-server/src/main/resources/application.yml` with your Git repository URL:
```yaml
spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/YOUR-USERNAME/YOUR-CONFIG-REPO.git
```
