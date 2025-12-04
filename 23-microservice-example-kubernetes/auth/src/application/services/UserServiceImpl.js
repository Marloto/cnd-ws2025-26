const debug = require('debug')('auth:service');
const UserService = require('../../domain/UserService');
const User = require('../../domain/User');

/**
 * Application Service: UserServiceImpl
 *
 * Implements the domain UserService interface.
 * Orchestrates domain logic with infrastructure through ports.
 */
class UserServiceImpl extends UserService {
    constructor(userRepository, passwordHasher, tokenGenerator) {
        super();
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.tokenGenerator = tokenGenerator;
    }

    /**
     * Register a new user
     */
    async register(username, email, password) {
        debug(`SERVICE: Registering new user: ${username}`);

        // Validation
        if (!username || username.trim().length < 3) {
            throw new Error('Username must be at least 3 characters');
        }

        if (!email || !this.isValidEmail(email)) {
            throw new Error('Invalid email format');
        }

        if (!password || password.length < 6) {
            throw new Error('Password must be at least 6 characters');
        }

        // Check if username or email already exists
        const usernameExists = await this.userRepository.existsByUsername(username);
        if (usernameExists) {
            debug(`SERVICE: Registration failed - username '${username}' already exists`);
            throw new Error('Username already exists');
        }

        const emailExists = await this.userRepository.existsByEmail(email);
        if (emailExists) {
            debug(`SERVICE: Registration failed - email '${email}' already exists`);
            throw new Error('Email already exists');
        }

        // Hash password and create user
        const hashedPassword = await this.passwordHasher.hash(password);
        const user = User.createNew(username, email, hashedPassword);

        // Save user
        const savedUser = await this.userRepository.save(user);
        debug(`SERVICE: Successfully registered user: ${username} (ID: ${savedUser.id})`);
        return savedUser;
    }

    /**
     * Login a user
     */
    async login(username, password) {
        debug(`SERVICE: Login attempt for user: ${username}`);

        if (!username || !password) {
            throw new Error('Username and password are required');
        }

        // Find user
        const user = await this.userRepository.findByUsername(username);
        if (!user) {
            debug(`SERVICE: Login failed - user '${username}' not found`);
            throw new Error('Invalid username or password');
        }

        // Verify password
        const passwordValid = await this.passwordHasher.compare(password, user.password);
        if (!passwordValid) {
            debug(`SERVICE: Login failed - invalid password for user '${username}'`);
            throw new Error('Invalid username or password');
        }

        // Generate token
        const token = await this.tokenGenerator.generate(user);
        debug(`SERVICE: Successfully logged in user: ${username} (ID: ${user.id})`);

        return { user, token };
    }

    /**
     * Change user email
     */
    async changeEmail(userId, newEmail) {
        debug(`SERVICE: Changing email for user ID: ${userId}`);

        if (!newEmail || !this.isValidEmail(newEmail)) {
            throw new Error('Invalid email format');
        }

        // Find user
        const user = await this.userRepository.findById(userId);
        if (!user) {
            debug(`SERVICE: Change email failed - user ID ${userId} not found`);
            throw new Error('User not found');
        }

        // Check if email already exists
        const emailExists = await this.userRepository.existsByEmail(newEmail);
        if (emailExists) {
            debug(`SERVICE: Change email failed - email '${newEmail}' already exists`);
            throw new Error('Email already exists');
        }

        // Update email using domain logic
        user.updateEmail(newEmail);

        // Persist changes
        const updatedUser = await this.userRepository.update(user);
        debug(`SERVICE: Successfully changed email for user: ${user.username}`);
        return updatedUser;
    }

    /**
     * Change user password
     */
    async changePassword(userId, oldPassword, newPassword) {
        debug(`SERVICE: Changing password for user ID: ${userId}`);

        if (!oldPassword || !newPassword) {
            throw new Error('Old password and new password are required');
        }

        if (newPassword.length < 6) {
            throw new Error('New password must be at least 6 characters');
        }

        // Find user
        const user = await this.userRepository.findById(userId);
        if (!user) {
            debug(`SERVICE: Change password failed - user ID ${userId} not found`);
            throw new Error('User not found');
        }

        // Verify old password
        const passwordValid = await this.passwordHasher.compare(oldPassword, user.password);
        if (!passwordValid) {
            debug(`SERVICE: Change password failed - incorrect old password for user: ${user.username}`);
            throw new Error('Old password is incorrect');
        }

        // Hash new password and update
        const hashedPassword = await this.passwordHasher.hash(newPassword);
        user.updatePassword(hashedPassword);

        // Persist changes
        const updatedUser = await this.userRepository.update(user);
        debug(`SERVICE: Successfully changed password for user: ${user.username}`);
        return updatedUser;
    }

    /**
     * Get user by ID
     */
    async getUserById(userId) {
        debug(`SERVICE: Getting user by ID: ${userId}`);
        const user = await this.userRepository.findById(userId);
        if (!user) {
            debug(`SERVICE: User not found with ID: ${userId}`);
            throw new Error('User not found');
        }
        debug(`SERVICE: Retrieved user: ${user.username}`);
        return user;
    }

    /**
     * Helper: Email validation
     */
    isValidEmail(email) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    }
}

module.exports = UserServiceImpl;
