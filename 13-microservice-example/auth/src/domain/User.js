/**
 * Domain Entity: User
 *
 * Pure domain object representing a user in the system.
 * Contains only business logic, no infrastructure concerns.
 */
class User {
    constructor(id, username, email, password) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password; // hashed password
    }

    /**
     * Factory method to create a new user from registration data
     */
    static createNew(username, email, hashedPassword) {
        return new User(null, username, email, hashedPassword);
    }

    /**
     * Update email (business rule: email must be valid)
     */
    updateEmail(newEmail) {
        if (!this.isValidEmail(newEmail)) {
            throw new Error('Invalid email format');
        }
        this.email = newEmail;
    }

    /**
     * Update password
     */
    updatePassword(hashedPassword) {
        this.password = hashedPassword;
    }

    /**
     * Simple email validation
     */
    isValidEmail(email) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    }

    /**
     * Convert to DTO (without password)
     */
    toDTO() {
        return {
            id: this.id,
            username: this.username,
            email: this.email
        };
    }
}

module.exports = User;
