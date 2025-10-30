# Flask REST API Example

A simple REST API example using Flask and SQLAlchemy, comparable to the Spring Boot REST example (02-spring-rest) and Express.js example (03-express-js).

## Technologies

- **Flask 3.1.0** - Lightweight Python web framework
- **SQLAlchemy** - Industry-standard Python ORM (Object-Relational Mapping)
- **SQLite** - Embedded database (similar to H2 in Spring Boot example)

## Setup

1. Create a virtual environment:
```bash
python3 -m venv venv
source venv/bin/activate  # On Windows: venv\Scripts\activate
```

2. Install dependencies:
```bash
pip install -r requirements.txt
```

3. Run the application:
```bash
python app.py
```

The server will start on http://localhost:5000

## API Endpoints

All endpoints follow RESTful conventions:

### List all posts
```bash
GET /posts
```

### Get a single post
```bash
GET /posts/{id}
```

### Create a new post
```bash
POST /posts
Content-Type: application/json

{
  "title": "My Post Title",
  "content": "Post content here"
}
```

### Update a post
```bash
PUT /posts/{id}
Content-Type: application/json

{
  "title": "Updated Title",
  "content": "Updated content"
}
```

### Delete a post
```bash
DELETE /posts/{id}
```

## Testing with curl

```bash
# Create a post
curl -X POST http://localhost:5000/posts \
  -H "Content-Type: application/json" \
  -d '{"title":"Test Post","content":"This is a test"}'

# List all posts
curl http://localhost:5000/posts

# Get a specific post (replace {id} with actual UUID)
curl http://localhost:5000/posts/{id}

# Update a post
curl -X PUT http://localhost:5000/posts/{id} \
  -H "Content-Type: application/json" \
  -d '{"title":"Updated","content":"New content"}'

# Delete a post
curl -X DELETE http://localhost:5000/posts/{id}
```

## Project Structure

```
03-flask-rest/
├── app.py              # Main Flask application with REST endpoints
├── models.py           # SQLAlchemy Post model
├── requirements.txt    # Python dependencies
└── README.md          # This file
```

## Configuration

The application uses environment variables for configuration:

- `DATABASE_URL` - Database connection string (defaults to `sqlite:///posts.db`)
- `PORT` - Server port (defaults to 5000)

## Notes

- The database is automatically created on first run
- Uses UUID for post IDs (similar to the Spring Boot example)
- Follows REST best practices with proper HTTP status codes (200, 201, 204, 404)
- SQLAlchemy is the de-facto standard ORM in Python, comparable to JPA/Hibernate in Java
