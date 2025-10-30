from flask import Flask, request, jsonify, url_for
from models import db, Post
from datetime import datetime
import os

app = Flask(__name__)

# Configuration
app.config['SQLALCHEMY_DATABASE_URI'] = os.getenv('DATABASE_URL', 'sqlite:///posts.db')
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False

# Initialize database
db.init_app(app)

# Create tables
with app.app_context():
    db.create_all()


@app.route('/posts', methods=['GET'])
def list_posts():
    """List all posts"""
    posts = Post.query.all()
    return jsonify([post.to_dict() for post in posts]), 200


@app.route('/posts/<string:post_id>', methods=['GET'])
def get_post(post_id):
    """Get a single post by ID"""
    post = Post.query.get(post_id)
    if post is None:
        return jsonify({'error': 'Post not found'}), 404
    return jsonify(post.to_dict()), 200


@app.route('/posts', methods=['POST'])
def create_post():
    """Create a new post"""
    data = request.get_json()

    if not data or 'title' not in data or 'content' not in data:
        return jsonify({'error': 'Title and content are required'}), 400

    post = Post(
        title=data['title'],
        content=data['content'],
        date=datetime.utcnow()
    )

    db.session.add(post)
    db.session.commit()

    # Return 201 Created with Location header
    return jsonify(post.to_dict()), 201, {'Location': url_for('get_post', post_id=post.id, _external=True)}


@app.route('/posts/<string:post_id>', methods=['PUT'])
def update_post(post_id):
    """Update an existing post"""
    post = Post.query.get(post_id)
    if post is None:
        return jsonify({'error': 'Post not found'}), 404

    data = request.get_json()
    if not data:
        return jsonify({'error': 'No data provided'}), 400

    if 'title' in data:
        post.title = data['title']
    if 'content' in data:
        post.content = data['content']
    post.date = datetime.utcnow()

    db.session.commit()
    return jsonify(post.to_dict()), 200


@app.route('/posts/<string:post_id>', methods=['DELETE'])
def delete_post(post_id):
    """Delete a post"""
    post = Post.query.get(post_id)
    if post is None:
        return jsonify({'error': 'Post not found'}), 404

    db.session.delete(post)
    db.session.commit()
    return '', 204


if __name__ == '__main__':
    port = int(os.getenv('PORT', 5000))
    app.run(host='0.0.0.0', port=port, debug=True)
