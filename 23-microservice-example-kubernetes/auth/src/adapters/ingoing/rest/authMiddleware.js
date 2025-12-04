/**
 * Auth Middleware
 *
 * Express middleware to protect routes with JWT authentication.
 */
function createAuthMiddleware(tokenGenerator) {
    return async (req, res, next) => {
        try {
            // Extract token from Authorization header
            const authHeader = req.headers.authorization;
            if (!authHeader || !authHeader.startsWith('Bearer ')) {
                return res.status(401).json({ error: 'No token provided' });
            }

            const token = authHeader.substring(7); // Remove 'Bearer ' prefix

            // Verify token
            const decoded = await tokenGenerator.verify(token);

            // Attach user info to request
            req.user = decoded;

            next();
        } catch (error) {
            return res.status(401).json({ error: 'Invalid or expired token' });
        }
    };
}

module.exports = createAuthMiddleware;
