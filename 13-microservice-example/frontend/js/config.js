/**
 * Configuration Module
 *
 * Defines API endpoints for the frontend.
 * This file can be replaced at deployment time using:
 * - Docker volumes/mounts
 * - Docker configs
 * - Kubernetes ConfigMaps
 */

const AUTH_API_BASE = 'http://localhost:3001/auth';
const POSTS_API_BASE = 'http://localhost:8080/posts';

console.log('Frontend configuration loaded:', {
    AUTH_API_BASE,
    POSTS_API_BASE
});
