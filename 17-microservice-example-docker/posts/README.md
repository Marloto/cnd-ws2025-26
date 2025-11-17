# Posts Service

A microservice for managing blog posts and comments built with Spring Boot and Java 21 using **Hexagonal Architecture** (Ports & Adapters).

## Architecture Overview

This service follows the Hexagonal Architecture pattern to keep the business logic independent of frameworks and infrastructure.

```
src/main/java/de/thi/inf/cnd/rest/
├── domain/                         # Core business logic (framework-agnostic)
│   ├── Post.java                   # Post entity with business rules
│   ├── Comment.java                # Comment entity
│   └── PostService.java            # Domain service interface
│
├── application/                    # Application layer (use cases)
│   ├── ports/                      # Interfaces (contracts)
│   │   ├── PostRepository.java          # Port for post persistence
│   │   └── PostEventPublisher.java      # Port for event publishing
│   └── services/
│       └── PostServiceImpl.java         # Implementation of domain service
│
└── adapters/                       # Infrastructure implementations
    ├── ingoing/                    # Entry points
    │   ├── rest/                   # REST API controllers
    │   │   ├── post/
    │   │   │   ├── RestPost.java        # Post endpoints
    │   │   │   ├── PostResponse.java    # DTOs
    │   │   │   └── CreatePostRequest.java
    │   │   ├── comment/
    │   │   │   ├── RestComment.java     # Comment endpoints
    │   │   │   └── CommentResponse.java
    │   │   └── auth/
    │   │       ├── JwtService.java      # JWT validation
    │   │       └── AuthenticatedUser.java
    │   └── grpc/
    │       └── GrpcStatisticService.java # gRPC service for statistics
    └── outgoing/                   # External services
        ├── rest/
        │   ├── JpaPostRepositoryImpl.java   # JPA implementation
        │   └── JpaPostCrudRepository.java   # Spring Data repository
        └── mqtt/
            ├── MqttPostPublisher.java       # MQTT event publisher
            └── PostPublishedEvent.java
```

## API Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/posts` | Get all posts | No |
| POST | `/posts` | Create a new post | Yes |
| GET | `/posts/:id` | Get a specific post | No |
| PUT | `/posts/:id` | Update a post | Yes |
| DELETE | `/posts/:id` | Delete a post | Yes |
| GET | `/posts/:id/comments` | Get comments for a post | No |
| POST | `/posts/:id/comments` | Add comment to a post | Yes |

## Prerequisites

- Java 21+ and Maven
- Auth service running on `http://localhost:3001`
- Mosquitto MQTT broker (via Docker)

## Installation

```bash
# Build the project (this will download dependencies and compile)
./mvnw clean package
```

## Running the Service

### 1. Start Mosquitto MQTT Broker with Docker
```bash
docker-compose up -d
```

### 2. Ensure Auth Service is Running

The Posts service requires the Auth service for JWT token validation:

```bash
cd ../auth
npm start
```

### 3. Start the Posts Service
```bash
./mvnw spring-boot:run
```

The service will start on `http://localhost:8080`

## Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `SERVER_PORT` | Server port | `8080` |
| `SPRING_DATASOURCE_URL` | H2 database location | `jdbc:h2:file:./data/posts` |
| `MQTT_BROKER_URL` | MQTT broker URL | `tcp://localhost:1883` |
| `JWT_SECRET` | Secret key for JWT validation (must match Auth service) | `your-secret-key-change-in-production` |

## Usage Examples

### Get all posts (no auth required)
```bash
curl -X GET http://localhost:8080/posts
```

### Create a new post (authentication required)

First, login to get a JWT token from the auth service:

```bash
# Login to get token
TOKEN=$(curl -s -X POST http://localhost:3001/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "testuser", "password": "password123"}' \
  | jq -r '.token')

# Create post
curl -X POST http://localhost:8080/posts \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "My First Post",
    "content": "This is the content of my first post"
  }'
```

Response:
```
HTTP/1.1 201 Created
Location: http://localhost:8080/posts/1
```

### Get a specific post
```bash
curl -X GET http://localhost:8080/posts/1
```

Response:
```json
{
  "id": 1,
  "title": "My First Post",
  "content": "This is the content of my first post",
  "author": "testuser",
  "createdAt": "2025-11-12T10:30:00",
  "updatedAt": "2025-11-12T10:30:00",
  "comments": []
}
```

### Update a post (authenticated)
```bash
curl -X PUT http://localhost:8080/posts/1 \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Updated Post Title",
    "content": "This content has been updated"
  }'
```

### Add a comment to a post (authenticated)
```bash
curl -X POST http://localhost:8080/posts/1/comments \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "author": "John Doe",
    "text": "Great post!"
  }'
```

### Get all comments for a post
```bash
curl -X GET http://localhost:8080/posts/1/comments
```

### Delete a post (authenticated)
```bash
curl -X DELETE http://localhost:8080/posts/1 \
  -H "Authorization: Bearer $TOKEN"
```

## Features

### JWT Authentication
The service validates JWT tokens issued by the Auth service. Include the token in the `Authorization` header as `Bearer <token>` for protected endpoints.

### Event Publishing (MQTT)
When a new post is created, the service publishes an event to the MQTT broker on the `posts/created` topic. This enables event-driven communication with other services.

### In-Memory Database (H2)
The service uses H2 database for storage, persisted to the filesystem. Data is stored in `./data/posts.mv.db`.

### gRPC Service
The service also exposes a gRPC endpoint for statistics (separate from the REST API).

## Testing

Use the included [test.http](test.http) file with REST Client extensions in VS Code or IntelliJ.

## Technology Stack

- **Spring Boot 3.4.1** - Application framework
- **Java 21** - Programming language
- **H2 Database** - In-memory/file-based SQL database
- **Spring Data JPA** - Data persistence
- **JWT (jjwt)** - Token-based authentication
- **gRPC** - RPC framework
- **MQTT (Eclipse Paho)** - Message broker integration
- **Lombok** - Reduce boilerplate code
