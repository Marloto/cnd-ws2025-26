const express = require('express');
const debug = require('debug')('auth:routes');
const router = express.Router();

/**
 * REST Adapter: User Routes
 *
 * Ingoing adapter that exposes HTTP endpoints.
 * Translates HTTP requests to domain service calls.
 */
function createUserRoutes(userService, authMiddleware) {

    /**
     * POST /auth/register
     * Register a new user
     */
    router.post('/auth/register', async (req, res) => {
        const { username, email } = req.body;
        debug(`REST: POST /auth/register - Registering user: ${username}`);
        try {
            const user = await userService.register(username, email, req.body.password);
            debug(`REST: POST /auth/register - Successfully registered user: ${username} (ID: ${user.id})`);
            res.status(201).json(user.toDTO());
        } catch (error) {
            debug(`REST: POST /auth/register - Failed to register user: ${error.message}`);
            res.status(400).json({ error: error.message });
        }
    });

    /**
     * POST /auth/login
     * Login a user
     */
    router.post('/auth/login', async (req, res) => {
        const { username } = req.body;
        debug(`REST: POST /auth/login - Login attempt for user: ${username}`);
        try {
            const { user, token } = await userService.login(username, req.body.password);
            debug(`REST: POST /auth/login - Successful login for user: ${username} (ID: ${user.id})`);
            res.json({
                user: user.toDTO(),
                token
            });
        } catch (error) {
            debug(`REST: POST /auth/login - Failed login attempt for user ${username}: ${error.message}`);
            res.status(401).json({ error: error.message });
        }
    });

    /**
     * GET /auth/me
     * Get current user info (protected)
     */
    router.get('/auth/me', authMiddleware, async (req, res) => {
        debug(`REST: GET /auth/me - Fetching user info for ID: ${req.user.userId}`);
        try {
            const user = await userService.getUserById(req.user.userId);
            debug(`REST: GET /auth/me - Retrieved user: ${user.username}`);
            res.json(user.toDTO());
        } catch (error) {
            debug(`REST: GET /auth/me - User not found: ${error.message}`);
            res.status(404).json({ error: error.message });
        }
    });

    /**
     * PATCH /auth/email
     * Change user email (protected)
     */
    router.patch('/auth/email', authMiddleware, async (req, res) => {
        const { email } = req.body;
        const userId = req.user.userId;
        debug(`REST: PATCH /auth/email - Changing email for user ID: ${userId} to ${email}`);
        try {
            const user = await userService.changeEmail(userId, email);
            debug(`REST: PATCH /auth/email - Successfully changed email for user: ${user.username}`);
            res.json(user.toDTO());
        } catch (error) {
            debug(`REST: PATCH /auth/email - Failed to change email: ${error.message}`);
            res.status(400).json({ error: error.message });
        }
    });

    /**
     * PATCH /auth/password
     * Change user password (protected)
     */
    router.patch('/auth/password', authMiddleware, async (req, res) => {
        const userId = req.user.userId;
        debug(`REST: PATCH /auth/password - Changing password for user ID: ${userId}`);
        try {
            const { oldPassword, newPassword } = req.body;
            const user = await userService.changePassword(userId, oldPassword, newPassword);
            debug(`REST: PATCH /auth/password - Successfully changed password for user: ${user.username}`);
            res.json(user.toDTO());
        } catch (error) {
            debug(`REST: PATCH /auth/password - Failed to change password: ${error.message}`);
            res.status(400).json({ error: error.message });
        }
    });

    return router;
}

module.exports = createUserRoutes;
