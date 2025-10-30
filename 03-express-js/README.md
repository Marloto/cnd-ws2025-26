# Express.js REST API Example

A simple REST API example using Express.js and Mongoose, comparable to the Spring Boot REST example (02-spring-rest) and Flask example (03-flask-rest).

## Technologies

- **Express.js 4.17.1** - Fast, minimalist web framework for Node.js
- **Mongoose 6.0.7** - Elegant MongoDB object modeling for Node.js (ODM - Object Document Mapper)
- **MongoDB** - NoSQL document database
- **Mocha/Chai** - Testing framework and assertion library

## Setup

1. Install dependencies:
```bash
npm install
```

2. Start MongoDB:
```bash
# Using Docker Compose (recommended)
docker-compose up -d

# Or install and start MongoDB locally
# macOS: brew services start mongodb-community
# Linux: sudo systemctl start mongod
```

3. Run the application:
```bash
npm start
```

The server will start on http://localhost:3000

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

### Update a post (partial update)
```bash
PATCH /posts/{id}
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

## Testing

Run the test suite with:
```bash
npm test
```

Tests are located in the `test/` directory and use Mocha, Chai, and chai-http.

## Testing with curl

```bash
# Create a post
curl -X POST http://localhost:3000/posts \
  -H "Content-Type: application/json" \
  -d '{"title":"Test Post","content":"This is a test"}'

# List all posts
curl http://localhost:3000/posts

# Get a specific post (replace {id} with actual MongoDB ObjectId)
curl http://localhost:3000/posts/{id}

# Update a post
curl -X PATCH http://localhost:3000/posts/{id} \
  -H "Content-Type: application/json" \
  -d '{"title":"Updated","content":"New content"}'

# Delete a post
curl -X DELETE http://localhost:3000/posts/{id}
```

## Project Structure

```
03-express-js/
├── app.js              # Main Express application and database connection
├── models/
│   └── post.js         # Mongoose Post model
├── routes/
│   └── post.js         # REST endpoints for posts
├── test/
│   └── testPost.js     # Test suite
├── package.json        # Node.js dependencies and scripts
├── docker-compose.yml  # MongoDB Docker configuration
└── README.md          # This file
```

## Configuration

The application uses environment variables for configuration:

- `MONGO_DB_URL` - MongoDB connection string (defaults to `mongodb://localhost:27017/example`)
- `PORT` - Server port (defaults to 3000)
- `DEBUG` - Debug output (set to `example:*` for all debug messages)

## Notes

- Uses MongoDB with Mongoose ODM (Object Document Mapper)
- MongoDB automatically generates `_id` fields (ObjectId type)
- Follows REST best practices with proper HTTP status codes (200, 204, 404)
- Uses PATCH for partial updates (vs PUT for full replacement)
- Includes test suite with Mocha and Chai
- Mongoose is the de-facto standard ODM for MongoDB in Node.js, comparable to JPA/Hibernate in Java or SQLAlchemy in Python
