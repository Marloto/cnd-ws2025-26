# Microservices Example with Docker

A fully containerized microservices architecture with authentication, posts/comments, and a frontend application. All services run in Docker containers with nginx as a reverse proxy.

## Architecture

```
microservice-example/
├── auth/                  # Authentication service (Node.js/Express/MongoDB)
├── posts/                 # Posts & comments service (Spring Boot/MySQL/MQTT)
├── frontend/              # Frontend application (HTML/CSS/JS)
├── nginx.conf             # Nginx reverse proxy configuration
├── docker-compose.yml     # Docker Compose orchestration
└── mosquitto.conf         # MQTT broker configuration
```

### Services

1. **Nginx Reverse Proxy** (Port 80)
   - Routes `/auth` to auth service
   - Routes `/posts` to posts service
   - Routes `/` to frontend
   - Single entry point for all services

2. **Auth Service** (Internal port 3001)
   - User registration and login
   - JWT token generation
   - Built with Node.js, Express, MongoDB
   - Hexagonal architecture

3. **Posts Service** (Internal port 8080)
   - Blog posts and comments
   - MQTT event publishing
   - Built with Spring Boot, MySQL
   - Hexagonal architecture

4. **Frontend** (Internal port 80)
   - Single-page application
   - Integrates auth and posts services via nginx
   - Modular JavaScript (auth.js, posts.js, config.js)

5. **MongoDB** (Port 27017)
   - Database for auth service
   - Persistent volume storage

6. **MySQL** (Internal port 3306)
   - Database for posts service
   - Persistent volume storage

7. **Mosquitto MQTT** (Ports 1883, 9001)
   - Event broker for posts service
   - Persistent volume storage

## Quick Start

### Prerequisites

- Docker and Docker Compose installed
- Optional: `.env` file to override default configuration (see Configuration section)

### 1. Start All Services

```bash
# From 17-microservice-example-docker directory
docker-compose up -d
```

This starts all services:
- Nginx reverse proxy
- Auth service
- Posts service
- Frontend
- MongoDB
- MySQL
- Mosquitto MQTT broker

### 2. Access the Application

Open your browser and navigate to:
- **Main Application**: http://localhost/
- **Auth API**: http://localhost/auth
- **Posts API**: http://localhost/posts

All requests are routed through the nginx reverse proxy on port 80.

## API Endpoints

Access all endpoints through the nginx reverse proxy at http://localhost

### Auth Service (http://localhost/auth)

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/auth/register` | Register new user | No |
| POST | `/auth/login` | Login and get JWT | No |
| GET | `/auth/me` | Get current user | Yes |
| PATCH | `/auth/email` | Change email | Yes |
| PATCH | `/auth/password` | Change password | Yes |
| GET | `/auth/health` | Health check | No |

### Posts Service (http://localhost/posts)

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/posts` | Get all posts | No |
| POST | `/posts` | Create post | Yes |
| GET | `/posts/{id}` | Get single post | No |
| GET | `/posts/{id}/comments` | Get post comments | No |
| POST | `/posts/{id}/comments` | Add comment | Yes |

## Configuration

### Environment Variables

The docker-compose.yml includes sensible defaults for development. You can optionally create a `.env` file to override these defaults:

**Default values (no .env file needed for development):**
- `SECRET=dev-secret-key-min-32-chars-change-in-prod`
- `MYSQL_USER=posts_user`
- `MYSQL_PASSWORD=posts_password`
- `MYSQL_ROOT_PASSWORD=root_password`
- `MYSQL_DATABASE=posts_db`

**For production**, create a `.env` file to override with secure values:

```env
# JWT Secret (shared across auth and posts services)
SECRET=your-secure-secret-key

# MySQL Database Configuration
MYSQL_USER=posts_user
MYSQL_PASSWORD=your-secure-password
MYSQL_ROOT_PASSWORD=your-secure-root-password
MYSQL_DATABASE=posts_db
```

### Frontend Configuration

The frontend configuration is automatically injected via Docker Compose configs. The API endpoints are set to use the nginx reverse proxy:

```javascript
const AUTH_API_BASE = 'http://localhost/auth';
const POSTS_API_BASE = 'http://localhost/posts';
```

This is defined in [docker-compose.yml](docker-compose.yml) under the `configs` section and overrides [frontend/js/config.js](frontend/js/config.js).

### Nginx Configuration

The nginx reverse proxy is configured in [nginx.conf](nginx.conf) to route requests:
- `/auth` → auth service (port 3001)
- `/posts` → posts service (port 8080)
- `/` → frontend (port 80)

## Development Workflow

1. (Optional) Create `.env` file to override default configuration
2. Start all services: `docker-compose up -d`
3. Open http://localhost in browser
4. Register/login and start creating posts
5. View logs: `docker-compose logs -f [service-name]`

### Rebuilding Services

After code changes, rebuild specific services:

```bash
# Rebuild and restart a specific service
docker-compose build auth
docker-compose up -d auth

# Rebuild all services
docker-compose build
docker-compose up -d
```

## Testing

### Auth Service

Use the [test.http](test.http) file with REST Client. Update the base URL to use the nginx proxy:

### Posts Service

Use the [test.http](test.http) file with REST Client. Update the base URL to use the nginx proxy:

## Managing Services

```bash
# View all running services
docker-compose ps

# View logs for all services
docker-compose logs -f

# View logs for specific service
docker-compose logs -f nginx
docker-compose logs -f auth

# Stop all services
docker-compose down

# Stop and remove volumes (clears all data)
docker-compose down -v

# Restart a specific service
docker-compose restart nginx
```

## Troubleshooting

### Port Already in Use

If port 80 is already in use, edit [docker-compose.yml](docker-compose.yml) to change the nginx port mapping:
```yaml
ports:
  - "8080:80"  # Access via http://localhost:8080
```
