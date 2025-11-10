/**
 * Domain Service: UserService
 *
 * Defines the business logic interface for user operations.
 * This is a port (interface) that will be implemented by the application layer.
 */
class UserService {
    /**
     * Register a new user
     * @param {string} username
     * @param {string} email
     * @param {string} password - plain text password
     * @returns {Promise<User>}
     */
    async register(username, email, password) {
        throw new Error('Method not implemented');
    }

    /**
     * Login a user
     * @param {string} username
     * @param {string} password
     * @returns {Promise<{user: User, token: string}>}
     */
    async login(username, password) {
        throw new Error('Method not implemented');
    }

    /**
     * Change user email
     * @param {string} userId
     * @param {string} newEmail
     * @returns {Promise<User>}
     */
    async changeEmail(userId, newEmail) {
        throw new Error('Method not implemented');
    }

    /**
     * Change user password
     * @param {string} userId
     * @param {string} oldPassword
     * @param {string} newPassword
     * @returns {Promise<User>}
     */
    async changePassword(userId, oldPassword, newPassword) {
        throw new Error('Method not implemented');
    }

    /**
     * Get user by ID
     * @param {string} userId
     * @returns {Promise<User>}
     */
    async getUserById(userId) {
        throw new Error('Method not implemented');
    }
}

module.exports = UserService;
