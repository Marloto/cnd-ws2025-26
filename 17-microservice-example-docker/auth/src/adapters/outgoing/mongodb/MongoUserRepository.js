const debug = require('debug')('auth:repository');
const UserRepository = require('../../../application/ports/UserRepository');
const User = require('../../../domain/User');
const UserModel = require('./UserSchema');

/**
 * Adapter: MongoUserRepository
 *
 * Implements UserRepository port using MongoDB/Mongoose.
 * Translates between domain User objects and Mongoose documents.
 */
class MongoUserRepository extends UserRepository {
    /**
     * Save a new user
     */
    async save(user) {
        debug(`REPOSITORY: Saving user: ${user.username}`);
        const userDoc = new UserModel({
            username: user.username,
            email: user.email,
            password: user.password
        });

        const savedDoc = await userDoc.save();
        debug(`REPOSITORY: User saved with ID: ${savedDoc._id}`);
        return this.toDomain(savedDoc);
    }

    /**
     * Find user by username
     */
    async findByUsername(username) {
        debug(`REPOSITORY: Finding user by username: ${username}`);
        const userDoc = await UserModel.findOne({ username });
        if (userDoc) {
            debug(`REPOSITORY: Found user: ${username}`);
        } else {
            debug(`REPOSITORY: User not found: ${username}`);
        }
        return userDoc ? this.toDomain(userDoc) : null;
    }

    /**
     * Find user by ID
     */
    async findById(id) {
        debug(`REPOSITORY: Finding user by ID: ${id}`);
        try {
            const userDoc = await UserModel.findById(id);
            if (userDoc) {
                debug(`REPOSITORY: Found user with ID: ${id}`);
            } else {
                debug(`REPOSITORY: User not found with ID: ${id}`);
            }
            return userDoc ? this.toDomain(userDoc) : null;
        } catch (error) {
            debug(`REPOSITORY: Error finding user by ID ${id}: ${error.message}`);
            return null;
        }
    }

    /**
     * Update an existing user
     */
    async update(user) {
        debug(`REPOSITORY: Updating user: ${user.username} (ID: ${user.id})`);
        const userDoc = await UserModel.findByIdAndUpdate(
            user.id,
            {
                username: user.username,
                email: user.email,
                password: user.password
            },
            { new: true, runValidators: true }
        );

        if (!userDoc) {
            debug(`REPOSITORY: Update failed - user not found with ID: ${user.id}`);
            throw new Error('User not found');
        }

        debug(`REPOSITORY: Successfully updated user: ${user.username}`);
        return this.toDomain(userDoc);
    }

    /**
     * Check if username exists
     */
    async existsByUsername(username) {
        debug(`REPOSITORY: Checking if username exists: ${username}`);
        const count = await UserModel.countDocuments({ username });
        debug(`REPOSITORY: Username '${username}' exists: ${count > 0}`);
        return count > 0;
    }

    /**
     * Check if email exists
     */
    async existsByEmail(email) {
        debug(`REPOSITORY: Checking if email exists: ${email}`);
        const count = await UserModel.countDocuments({ email });
        debug(`REPOSITORY: Email '${email}' exists: ${count > 0}`);
        return count > 0;
    }

    /**
     * Convert Mongoose document to domain User
     */
    toDomain(userDoc) {
        return new User(
            userDoc._id.toString(),
            userDoc.username,
            userDoc.email,
            userDoc.password
        );
    }
}

module.exports = MongoUserRepository;
