/**
 * Port: UserRepository
 *
 * Output port for user persistence.
 * Defines the interface that outgoing adapters must implement.
 */
class UserRepository {
    /**
     * Save a new user
     * @param {User} user
     * @returns {Promise<User>} user with generated ID
     */
    async save(user) {
        throw new Error('Method not implemented');
    }

    /**
     * Find user by username
     * @param {string} username
     * @returns {Promise<User|null>}
     */
    async findByUsername(username) {
        throw new Error('Method not implemented');
    }

    /**
     * Find user by ID
     * @param {string} id
     * @returns {Promise<User|null>}
     */
    async findById(id) {
        throw new Error('Method not implemented');
    }

    /**
     * Update an existing user
     * @param {User} user
     * @returns {Promise<User>}
     */
    async update(user) {
        throw new Error('Method not implemented');
    }

    /**
     * Check if username exists
     * @param {string} username
     * @returns {Promise<boolean>}
     */
    async existsByUsername(username) {
        throw new Error('Method not implemented');
    }

    /**
     * Check if email exists
     * @param {string} email
     * @returns {Promise<boolean>}
     */
    async existsByEmail(email) {
        throw new Error('Method not implemented');
    }
}

module.exports = UserRepository;
