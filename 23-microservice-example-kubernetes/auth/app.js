const express = require('express');
const mongoose = require('mongoose');
const debug = require('debug')('auth:app');

// Orchestration: Wire up all dependencies
const UserServiceImpl = require('./src/application/services/UserServiceImpl');
const MongoUserRepository = require('./src/adapters/outgoing/mongodb/MongoUserRepository');
const BcryptPasswordHasher = require('./src/adapters/outgoing/bcrypt/BcryptPasswordHasher');
const JwtTokenGenerator = require('./src/adapters/outgoing/jwt/JwtTokenGenerator');
const createUserRoutes = require('./src/adapters/ingoing/rest/userRoutes');
const createAuthMiddleware = require('./src/adapters/ingoing/rest/authMiddleware');

// Configuration
const mongoDbUrl = process.env.MONGO_DB_URL || 'mongodb://localhost:27017/auth';
const jwtSecret = process.env.JWT_SECRET || 'your-secret-key-change-in-production';
const port = process.env.PORT || 3001;

const app = express();

debug(`Connecting to database: ${mongoDbUrl}`);

mongoose
    .connect(mongoDbUrl, { useNewUrlParser: true })
    .then(() => {
        debug('Database connected');

        // === Dependency Injection / Orchestration ===
        // This is where we wire up the hexagonal architecture
        // without a DI container like Spring Boot

        // 1. Create outgoing adapters (implementations of ports)
        const userRepository = new MongoUserRepository();
        const passwordHasher = new BcryptPasswordHasher();
        const tokenGenerator = new JwtTokenGenerator(jwtSecret);

        // 2. Create application service with injected dependencies
        const userService = new UserServiceImpl(
            userRepository,
            passwordHasher,
            tokenGenerator
        );

        // 3. Create ingoing adapters (REST controllers)
        const authMiddleware = createAuthMiddleware(tokenGenerator);
        const userRoutes = createUserRoutes(userService, authMiddleware);

        // 4. Configure Express

        // CORS middleware
        app.use((req, res, next) => {
            res.header('Access-Control-Allow-Origin', '*');
            res.header('Access-Control-Allow-Headers', 'Content-Type, Authorization');
            res.header('Access-Control-Allow-Methods', 'GET, POST, PATCH, DELETE, OPTIONS');

            // Handle preflight requests
            if (req.method === 'OPTIONS') {
                return res.sendStatus(200);
            }

            next();
        });

        app.use(express.json());
        app.use(userRoutes);

        // Liveness probe - checks if the application is alive
        // Returns 200 if the app is running, should NOT check dependencies
        app.get('/health/liveness', (_req, res) => {
            res.status(200).json({
                status: 'UP',
                service: 'auth-service',
                timestamp: new Date().toISOString()
            });
        });

        // Readiness probe - checks if the application is ready to serve traffic
        // Returns 200 only if all dependencies (database) are available
        app.get('/health/readiness', async (_req, res) => {
            try {
                // Check database connection
                const dbState = mongoose.connection.readyState;
                // 0 = disconnected, 1 = connected, 2 = connecting, 3 = disconnecting

                if (dbState === 1) {
                    // Optionally ping the database to ensure it's responsive
                    await mongoose.connection.db.admin().ping();

                    res.status(200).json({
                        status: 'UP',
                        service: 'auth-service',
                        checks: {
                            database: 'UP'
                        },
                        timestamp: new Date().toISOString()
                    });
                } else {
                    res.status(503).json({
                        status: 'DOWN',
                        service: 'auth-service',
                        checks: {
                            database: 'DOWN'
                        },
                        timestamp: new Date().toISOString()
                    });
                }
            } catch (error) {
                res.status(503).json({
                    status: 'DOWN',
                    service: 'auth-service',
                    checks: {
                        database: 'DOWN'
                    },
                    error: error.message,
                    timestamp: new Date().toISOString()
                });
            }
        });

        // General health endpoint (backward compatibility)
        app.get('/health', (req, res) => {
            res.json({ status: 'UP', service: 'auth-service' });
        });

        // Start server
        app.listen(port, () => {
            debug(`Auth service started on port ${port}`);
            app.emit('started');
            app.started = true;
        });
    })
    .catch((error) => {
        console.error('Failed to connect to database:', error);
        process.exit(1);
    });

module.exports = app;
