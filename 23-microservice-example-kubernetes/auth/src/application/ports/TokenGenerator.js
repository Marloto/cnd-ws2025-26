/**
 * Port: TokenGenerator
 *
 * Output port for JWT token generation.
 * Abstracts the token implementation.
 */
class TokenGenerator {
    /**
     * Generate a JWT token for a user
     * @param {User} user
     * @returns {Promise<string>} JWT token
     */
    async generate(user) {
        throw new Error('Method not implemented');
    }

    /**
     * Verify and decode a JWT token
     * @param {string} token
     * @returns {Promise<object>} decoded token payload
     */
    async verify(token) {
        throw new Error('Method not implemented');
    }
}

module.exports = TokenGenerator;
