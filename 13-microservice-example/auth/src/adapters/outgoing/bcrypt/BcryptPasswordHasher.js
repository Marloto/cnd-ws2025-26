const bcrypt = require('bcrypt');
const PasswordHasher = require('../../../application/ports/PasswordHasher');

/**
 * Adapter: BcryptPasswordHasher
 *
 * Implements PasswordHasher port using bcrypt library.
 */
class BcryptPasswordHasher extends PasswordHasher {
    constructor(saltRounds = 10) {
        super();
        this.saltRounds = saltRounds;
    }

    /**
     * Hash a plain text password
     */
    async hash(plainPassword) {
        return await bcrypt.hash(plainPassword, this.saltRounds);
    }

    /**
     * Compare plain password with hash
     */
    async compare(plainPassword, hashedPassword) {
        return await bcrypt.compare(plainPassword, hashedPassword);
    }
}

module.exports = BcryptPasswordHasher;
