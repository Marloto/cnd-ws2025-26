# Frontend - Blog Platform

A simple, modular frontend application that integrates the **Auth Service** and **Posts Service** microservices.

## Architecture

The frontend is organized into separate concerns:

```
frontend/
├── index.html           # Main HTML page
├── css/
│   └── styles.css      # All styling
└── js/
    ├── auth.js         # Authentication logic
    └── posts.js        # Posts & comments logic
```

## Features

### Authentication (`auth.js`)
- Login and registration with modal UI
- JWT token management (stored in localStorage)
- Auth state persistence across page reloads
- `requireAuth()` function to protect actions
- Automatic token injection into API requests

### Posts & Comments (`posts.js`)
- Display all blog posts
- Create new posts (requires authentication)
- Load and display comments per post
- Add comments (requires authentication)

### User Experience
- **Add New Post**: Checks authentication first, shows login modal if needed
- **Post Comment**: Checks authentication first, shows login modal if needed
- **Auth Modal**: Tabbed interface for Login/Register
- **User Info**: Displays current username and logout button when logged in

## API Endpoints

### Auth Service (Port 3001)
- `POST /auth/register` - Register new user
- `POST /auth/login` - Login and get JWT token

### Posts Service (Port 8080)
- `GET /posts` - Get all posts
- `POST /posts` - Create new post (requires auth)
- `GET /posts/:id/comments` - Get comments for a post
- `POST /posts/:id/comments` - Add comment to post (requires auth)

## Setup

### Prerequisites
- Auth service running on `http://localhost:3001`
- Posts service running on `http://localhost:8080`

### Running the Frontend

The simplest way is to use a local web server:

#### Option 1: Python HTTP Server
```bash
cd frontend
python3 -m http.server 8000
```
Then open: `http://localhost:8000`

#### Option 2: Node.js http-server
```bash
npm install -g http-server
cd frontend
http-server -p 8000
```
Then open: `http://localhost:8000`

#### Option 3: VS Code Live Server
1. Install "Live Server" extension in VS Code
2. Right-click `index.html` and select "Open with Live Server"

## Configuration

If your services run on different ports, update the constants in the JavaScript files:

**auth.js:**
```javascript
const AUTH_API_BASE = 'http://localhost:3001/auth';
```

**posts.js:**
```javascript
const POSTS_API_BASE = 'http://localhost:8080/posts';
```

## CORS Configuration

Make sure both backend services have CORS enabled to allow requests from the frontend origin.

For the **Auth Service** (Node.js/Express), add this before your routes:
```javascript
app.use((req, res, next) => {
    res.header('Access-Control-Allow-Origin', '*');
    res.header('Access-Control-Allow-Headers', 'Content-Type, Authorization');
    res.header('Access-Control-Allow-Methods', 'GET, POST, PATCH, DELETE');
    next();
});
```

For the **Posts Service** (Spring Boot), add this configuration:
```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PATCH", "DELETE")
                .allowedHeaders("*");
    }
}
```

## Usage Flow

1. **Open the frontend** in your browser
2. **View posts** without authentication (public read access)
3. **Click "Add New Post"** → Authentication modal appears if not logged in
4. **Login or Register** in the modal
5. **Create posts and comments** once authenticated
6. **Logout** from the header when done

## Module Details

### auth.js Functions

| Function | Description |
|----------|-------------|
| `initAuth()` | Initialize auth module and restore session |
| `isAuthenticated()` | Check if user is logged in |
| `requireAuth(callback)` | Execute callback only if authenticated |
| `login()` | Login user with username/password |
| `register()` | Register new user |
| `logout()` | Logout and clear session |
| `addAuthHeader(options)` | Add Authorization header to fetch options |

### posts.js Functions

| Function | Description |
|----------|-------------|
| `initPosts()` | Initialize posts module and load posts |
| `loadPosts()` | Load all posts from API |
| `showAddPostModal()` | Show create post modal (with auth check) |
| `createPost()` | Create new post |
| `loadComments(postId)` | Load comments for a post |
| `addComment(postId)` | Add comment to post (with auth check) |

## Security

- JWT tokens are stored in localStorage
- Tokens are automatically included in authenticated requests via `Authorization: Bearer` header
- Protected actions require authentication before execution
- Passwords are sent over HTTPS (use HTTPS in production!)

## Browser Compatibility

- Requires ES6+ support (modern browsers)
- Uses Fetch API for HTTP requests
- Bootstrap 5 for UI components

## Future Enhancements

- Token expiration handling with refresh
- User profile page
- Edit/delete posts and comments
- Rich text editor for post content
- Image upload support
- Real-time updates with WebSockets
