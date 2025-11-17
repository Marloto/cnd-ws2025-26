const jwt = require('jsonwebtoken');
const TokenGenerator = require('../../../application/ports/TokenGenerator');

/**
 * Adapter: JwtTokenGenerator
 *
 * Implements TokenGenerator port using jsonwebtoken library.
 */
class JwtTokenGenerator extends TokenGenerator {
    constructor(secret, expiresIn = '24h') {
        super();
        this.secret = secret;
        this.expiresIn = expiresIn;
    }

    /**
     * Generate a JWT token for a user
     */
    async generate(user) {
        const payload = {
            userId: user.id,
            username: user.username,
            email: user.email
        };

        return jwt.sign(payload, this.secret, { expiresIn: this.expiresIn });
    }

    /**
     * Verify and decode a JWT token
     */
    async verify(token) {
        try {
            return jwt.verify(token, this.secret);
        } catch (error) {
            throw new Error('Invalid or expired token');
        }
    }
}

module.exports = JwtTokenGenerator;
