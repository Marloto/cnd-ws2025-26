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

### Key Concepts

**Domain Layer**: Pure business logic, no dependencies on frameworks
- `User`: Entity with business rules (email validation, etc.)
- `UserService`: Interface defining business operations

**Application Layer**: Orchestrates domain logic and defines ports
- `UserServiceImpl`: Implements business logic using ports
- `Ports`: Interfaces that adapters must implement

**Adapters**: Infrastructure implementations
- **Ingoing**: REST API controllers (how users interact with the service)
- **Outgoing**: MongoDB, Bcrypt, JWT (how the service interacts with external systems)

**Orchestration** (`app.js`): Manual dependency injection
- Creates all adapter instances
- Wires them together
- Configures Express and starts the server

## Features

- User registration with validation
- User login with JWT token generation
- Email and password change (authenticated)
- Password hashing with bcrypt
- JWT-based authentication
- MongoDB persistence with Mongoose

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

## Hexagonal Architecture Benefits

1. **Framework Independence**: Core business logic doesn't depend on Express, Mongoose, or any framework
2. **Testability**: Each layer can be tested independently with mocks
3. **Flexibility**: Easy to swap implementations (e.g., switch from MongoDB to PostgreSQL)
4. **Clear Boundaries**: Separation of concerns between business logic and infrastructure
5. **Maintainability**: Changes to infrastructure don't affect business logic

## Dependency Injection without Spring Boot

Unlike Spring Boot's automatic dependency injection, Node.js requires manual wiring. This is done in `app.js`:

```javascript
// 1. Create adapters
const userRepository = new MongoUserRepository();
const passwordHasher = new BcryptPasswordHasher();
const tokenGenerator = new JwtTokenGenerator(jwtSecret);

// 2. Inject into application service
const userService = new UserServiceImpl(
    userRepository,
    passwordHasher,
    tokenGenerator
);

// 3. Create REST routes with injected service
const userRoutes = createUserRoutes(userService, authMiddleware);
```

This explicit orchestration replaces Spring Boot's `@Autowired` and `@Component` annotations.

## Security Notes

- **Production**: Change `JWT_SECRET` to a strong random value
- Passwords are hashed with bcrypt (10 salt rounds)
- JWT tokens expire after 24 hours (configurable)
- Protected routes require valid JWT in Authorization header

## License

ISC
