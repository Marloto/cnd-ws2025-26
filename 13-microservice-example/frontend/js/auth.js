/**
 * Authentication Module
 * Handles user authentication, token management, and auth UI
 */

// Configuration is loaded from config.js
const TOKEN_KEY = 'jwt_token';
const USER_KEY = 'user_info';

// Authentication state
let currentUser = null;
let authToken = null;
let authModal = null;
let authRequiredCallback = null;

/**
 * Initialize auth module
 */
function initAuth() {
    // Load saved token and user from localStorage
    authToken = localStorage.getItem(TOKEN_KEY);
    const savedUser = localStorage.getItem(USER_KEY);

    if (savedUser) {
        currentUser = JSON.parse(savedUser);
        updateAuthUI();
    }

    // Initialize auth modal
    authModal = new bootstrap.Modal(document.getElementById('authModal'));

    // Setup tab switching
    document.querySelectorAll('.auth-tab').forEach(tab => {
        tab.addEventListener('click', () => switchAuthTab(tab.dataset.tab));
    });
}

/**
 * Check if user is authenticated
 */
function isAuthenticated() {
    return authToken !== null && currentUser !== null;
}

/**
 * Get current auth token
 */
function getAuthToken() {
    return authToken;
}

/**
 * Require authentication before executing callback
 * If not authenticated, shows login modal
 */
function requireAuth(callback) {
    if (isAuthenticated()) {
        callback();
    } else {
        authRequiredCallback = callback;
        showAuthModal();
    }
}

/**
 * Show authentication modal
 */
function showAuthModal() {
    authModal.show();
    switchAuthTab('login');
    clearAuthErrors();
}

/**
 * Hide authentication modal
 */
function hideAuthModal() {
    authModal.hide();
}

/**
 * Switch between login and register tabs
 */
function switchAuthTab(tab) {
    // Update tabs
    document.querySelectorAll('.auth-tab').forEach(t => {
        t.classList.toggle('active', t.dataset.tab === tab);
    });

    // Update forms
    document.querySelectorAll('.auth-form').forEach(f => {
        f.classList.toggle('active', f.id === `${tab}Form`);
    });

    clearAuthErrors();
}

/**
 * Register a new user
 */
async function register() {
    const username = document.getElementById('registerUsername').value.trim();
    const email = document.getElementById('registerEmail').value.trim();
    const password = document.getElementById('registerPassword').value;

    if (!username || !email || !password) {
        showAuthError('register', 'Please fill in all fields');
        return;
    }

    try {
        const response = await fetch(`${AUTH_API_BASE}/register`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ username, email, password })
        });

        if (response.ok) {
            // Registration successful, now login
            showAuthError('register', 'Registration successful! Logging you in...', 'success');
            setTimeout(() => {
                login(username, password);
            }, 1000);
        } else {
            const error = await response.json();
            showAuthError('register', error.error || 'Registration failed');
        }
    } catch (error) {
        console.error('Registration error:', error);
        showAuthError('register', 'Registration failed. Please try again.');
    }
}

/**
 * Login user
 */
async function login(username, password) {
    // If called from form
    if (!username) {
        username = document.getElementById('loginUsername').value.trim();
        password = document.getElementById('loginPassword').value;
    }

    if (!username || !password) {
        showAuthError('login', 'Please fill in all fields');
        return;
    }

    try {
        const response = await fetch(`${AUTH_API_BASE}/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ username, password })
        });

        if (response.ok) {
            const data = await response.json();

            // Save token and user info
            authToken = data.token;
            currentUser = data.user;

            localStorage.setItem(TOKEN_KEY, authToken);
            localStorage.setItem(USER_KEY, JSON.stringify(currentUser));

            // Update UI
            updateAuthUI();

            // Clear errors and forms
            clearAuthErrors();
            document.getElementById('loginForm').querySelector('form').reset();
            document.getElementById('registerForm').querySelector('form').reset();

            // Close modal
            hideAuthModal();

            // Execute pending callback if any
            if (authRequiredCallback) {
                const callback = authRequiredCallback;
                authRequiredCallback = null;
                callback();
            }
        } else {
            const error = await response.json();
            showAuthError('login', error.error || 'Login failed');
        }
    } catch (error) {
        console.error('Login error:', error);
        showAuthError('login', 'Login failed. Please try again.');
    }
}

/**
 * Logout user
 */
function logout() {
    authToken = null;
    currentUser = null;
    authRequiredCallback = null;

    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);

    updateAuthUI();

    // Reload page to clear any user-specific content
    window.location.reload();
}

/**
 * Update authentication UI
 */
function updateAuthUI() {
    const userInfoEl = document.getElementById('userInfo');
    const loginBtn = document.getElementById('loginBtn');
    const usernameEl = document.getElementById('currentUsername');

    if (isAuthenticated()) {
        userInfoEl.classList.add('logged-in');
        loginBtn.style.display = 'none';
        usernameEl.textContent = currentUser.username;
    } else {
        userInfoEl.classList.remove('logged-in');
        loginBtn.style.display = 'block';
        usernameEl.textContent = '';
    }
}

/**
 * Show authentication error
 */
function showAuthError(form, message, type = 'error') {
    const errorEl = document.getElementById(`${form}Error`);
    errorEl.textContent = message;
    errorEl.classList.add('show');

    if (type === 'success') {
        errorEl.style.backgroundColor = '#d1e7dd';
        errorEl.style.color = '#0f5132';
        errorEl.style.borderColor = '#badbcc';
    } else {
        errorEl.style.backgroundColor = '#f8d7da';
        errorEl.style.color = '#dc3545';
        errorEl.style.borderColor = '#f5c2c7';
    }
}

/**
 * Clear authentication errors
 */
function clearAuthErrors() {
    document.querySelectorAll('.error-message').forEach(el => {
        el.classList.remove('show');
    });
}

/**
 * Add Authorization header to fetch options
 */
function addAuthHeader(options = {}) {
    if (!options.headers) {
        options.headers = {};
    }

    if (authToken) {
        options.headers['Authorization'] = `Bearer ${authToken}`;
    }

    return options;
}

// Initialize when DOM is ready
document.addEventListener('DOMContentLoaded', initAuth);
