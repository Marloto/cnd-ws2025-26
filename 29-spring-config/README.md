# Config Server

Use configuration from git repositories. Currently uses test repo: https://github.com/Marloto/spring-boot-config-example.git

## Usage

```
cd config-server
mvn spring-boot:run

# run for test (see configurations)

curl -v http://localhost:8888/cnd01/development
curl -v http://localhost:8888/cnd01/productive

# other terminal

cd config-client
mvn spring-boot:run

# run for test

curl http://localhost:8080/hello # prints Hello, World
curl http://localhost:8080/hello-refreshable # prints Hello, World (refreshable)

# close client and test again

cd config-client
mvn spring-boot:run -Dspring-boot.run.profiles=productive

# run for test

curl http://localhost:8080/hello # prints Hello, Universe
curl http://localhost:8080/hello-refreshable # prints Hello, Universe (refreshable)
```

## Configuration Refresh

The config-client demonstrates two approaches to configuration:

### 1. Static Configuration (`/hello`)
- Uses `@Value` annotation directly in the controller
- **Does NOT refresh** when configuration changes
- Requires application restart to pick up new values

### 2. Refreshable Configuration (`/hello-refreshable`)
- Uses `@RefreshScope` bean with `@Value` annotation
- **Can be refreshed** without restart using the actuator refresh endpoint

To refresh configuration without restart:
```bash
# Make changes to the config in Git repository, then:
curl -X POST http://localhost:8080/actuator/refresh

# Test the endpoints again:
curl http://localhost:8080/hello              # Still shows old value
curl http://localhost:8080/hello-refreshable  # Shows new value!
```

**Note:** Only beans annotated with `@RefreshScope` or `@ConfigurationProperties` will be refreshed. Regular `@Value` fields in non-refreshable beans will keep their old values until restart.
```