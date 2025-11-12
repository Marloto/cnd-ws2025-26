# Authentication Service

A microservice for user authentication built with Node.js, Express, and MongoDB using **Hexagonal Architecture** (Ports & Adapters).

## Architecture Overview

This service follows the Hexagonal Architecture pattern to keep the business logic independent of frameworks and infrastructure.

```
src/
├── domain/                    # Core business logic (framework-agnostic)
│   ├── User.js               # User entity with business rules
│   └── UserService.js        # Domain service interface
│
├── application/              # Application layer (use cases)
│   ├── ports/               # Interfaces (contracts)
│   │   ├── UserRepository.js      # Port for user persistence
│   │   ├── PasswordHasher.js      # Port for password hashing
│   │   └── TokenGenerator.js      # Port for JWT generation
│   └── services/
│       └── UserServiceImpl.js     # Implementation of domain service
│
└── adapters/                # Infrastructure implementations
    ├── ingoing/            # Entry points (controllers)
    │   └── rest/
    │       ├── userRoutes.js      # REST API endpoints
    │       └── authMiddleware.js  # JWT authentication middleware
    └── outgoing/           # External services
        └── mongodb/
            ├── UserSchema.js           # Mongoose schema
            ├── MongoUserRepository.js  # MongoDB implementation
            ├── BcryptPasswordHasher.js # Bcrypt implementation
            └── JwtTokenGenerator.js    # JWT implementation
```

## API Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/auth/register` | Register a new user | No |
| POST | `/auth/login` | Login and get JWT token | No |
| GET | `/auth/me` | Get current user info | Yes |
| PATCH | `/auth/email` | Change user email | Yes |
| PATCH | `/auth/password` | Change user password | Yes |
| GET | `/health` | Health check | No |

## Prerequisites

- Node.js 14+ and npm
- MongoDB (via Docker or local installation)

## Installation

```bash
# Install dependencies
npm install
```

## Running the Service

### 1. Start MongoDB with Docker
```bash
docker-compose up -d
```

### 2. Start the auth service
```bash
npm start
```

The service will start on `http://localhost:3001`

## Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `MONGO_DB_URL` | MongoDB connection string | `mongodb://localhost:27017/auth` |
| `JWT_SECRET` | Secret key for JWT signing | `your-secret-key-change-in-production` |
| `PORT` | Server port | `3001` |

## Usage Examples

### Register a new user
```bash
curl -X POST http://localhost:3001/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "email": "john@example.com",
    "password": "securepass123"
  }'
```

Response:
```json
{
  "id": "507f1f77bcf86cd799439011",
  "username": "johndoe",
  "email": "john@example.com"
}
```

### Login
```bash
curl -X POST http://localhost:3001/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "password": "securepass123"
  }'
```

Response:
```json
{
  "user": {
    "id": "507f1f77bcf86cd799439011",
    "username": "johndoe",
    "email": "john@example.com"
  },
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### Get current user (authenticated)
```bash
curl -X GET http://localhost:3001/auth/me \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Change email (authenticated)
```bash
curl -X PATCH http://localhost:3001/auth/email \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "newemail@example.com"
  }'
```

### Change password (authenticated)
```bash
curl -X PATCH http://localhost:3001/auth/password \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "oldPassword": "securepass123",
    "newPassword": "newsecurepass456"
  }'
```

## Testing

Use the included `test.http` file with REST Client extensions in VS Code or IntelliJ.
