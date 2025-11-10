const mongoose = require('mongoose');

/**
 * Mongoose Schema for User entity
 *
 * This is infrastructure code - not part of domain.
 */
const userSchema = mongoose.Schema({
    username: {
        type: String,
        required: true,
        unique: true,
        trim: true,
        minlength: 3
    },
    email: {
        type: String,
        required: true,
        unique: true,
        trim: true,
        lowercase: true
    },
    password: {
        type: String,
        required: true
    }
}, {
    timestamps: true // adds createdAt and updatedAt
});

module.exports = mongoose.model('User', userSchema);
