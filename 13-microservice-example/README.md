# Microservices Example

A simple microservices architecture example with authentication, posts/comments, and a frontend application. Docker Compose right now only to help with starting MQTT Broker and Database.

## Architecture

```
microservice-example/
├── auth/                  # Authentication service (Node.js/Express/MongoDB)
├── posts/                 # Posts & comments service (Spring Boot/MQTT)
├── frontend/              # Frontend application (HTML/CSS/JS)
```

### Services

1. **Auth Service** (Port 3001)
   - User registration and login
   - JWT token generation
   - Built with Node.js, Express, MongoDB
   - Hexagonal architecture

2. **Posts Service** (Port 8080)
   - Blog posts and comments
   - MQTT event publishing
   - Built with Spring Boot
   - Hexagonal architecture

3. **Frontend** (Port 8000)
   - Single-page application
   - Integrates auth and posts services
   - Modular JavaScript (auth.js, posts.js, config.js)

## Quick Start

### 1. Start Infrastructure Services

```bash
# From microservice-example directory
docker-compose up -d
```

This starts:
- MongoDB (port 27017) - for auth service
- Mosquitto MQTT broker (ports 1883, 9001) - for posts service

### 2. Start Auth Service

```bash
cd auth
npm install
npm start
```

Auth service runs on http://localhost:3001

### 3. Start Posts Service

```bash
cd posts
./mvnw spring-boot:run
```

Posts service runs on http://localhost:8080

### 4. Start Frontend

```bash
cd frontend
python3 -m http.server 8000
```

Frontend runs on http://localhost:8000

## API Endpoints

### Auth Service (http://localhost:3001)

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/auth/register` | Register new user | No |
| POST | `/auth/login` | Login and get JWT | No |
| GET | `/auth/me` | Get current user | Yes |
| PATCH | `/auth/email` | Change email | Yes |
| PATCH | `/auth/password` | Change password | Yes |
| GET | `/health` | Health check | No |

### Posts Service (http://localhost:8080)

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/posts` | Get all posts | No |
| POST | `/posts` | Create post | Yes |
| GET | `/posts/{id}` | Get single post | No |
| GET | `/posts/{id}/comments` | Get post comments | No |
| POST | `/posts/{id}/comments` | Add comment | Yes |

## Configuration

### Frontend Configuration

Edit [frontend/js/config.js](frontend/js/config.js) to change API endpoints:

```javascript
const AUTH_API_BASE = 'http://localhost:3001/auth';
const POSTS_API_BASE = 'http://localhost:8080/posts';
```

### Auth Service Configuration

Environment variables:
- `MONGO_DB_URL` - MongoDB connection (default: `mongodb://localhost:27017/auth`)
- `JWT_SECRET` - JWT signing key (default: `your-secret-key-change-in-production`)
- `PORT` - Service port (default: `3001`)

### Posts Service Configuration

Edit [posts/src/main/resources/application.yml](posts/src/main/resources/application.yml):
- MongoDB connection
- MQTT broker settings
- Server port
- JWT Secret

## Development Workflow

1. Start infrastructure: `docker-compose up -d`
2. Start both backend services (auth and posts)
3. Start frontend server
4. Open http://localhost:8000 in browser
5. Register/login and start creating posts

## Testing

### Auth Service

Use the [test.http](auth/test.http) file with REST Client for quick tests.

### Posts Service

Use the [test.http](posts/test.http) file with REST Client for quick tests.

## Stopping Services

```bash
# Stop infrastructure
docker-compose down
```

Stop services with Ctrl+C in their terminals
