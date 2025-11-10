/**
 * Port: PasswordHasher
 *
 * Output port for password hashing operations.
 * Abstracts the cryptographic implementation.
 */
class PasswordHasher {
    /**
     * Hash a plain text password
     * @param {string} plainPassword
     * @returns {Promise<string>} hashed password
     */
    async hash(plainPassword) {
        throw new Error('Method not implemented');
    }

    /**
     * Compare plain password with hash
     * @param {string} plainPassword
     * @param {string} hashedPassword
     * @returns {Promise<boolean>}
     */
    async compare(plainPassword, hashedPassword) {
        throw new Error('Method not implemented');
    }
}

module.exports = PasswordHasher;
