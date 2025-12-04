# Frontend - Blog Platform

A simple frontend application that integrates the **Auth Service** and **Posts Service** microservices.

## Architecture

The frontend is organized into separate concerns:

```
frontend/
├── index.html          # Main HTML page
├── css/
│   └── styles.css      # All styling
└── js/
    ├── config.js       # Endpoint config
    ├── auth.js         # Authentication logic
    └── posts.js        # Posts & comments logic
```

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

