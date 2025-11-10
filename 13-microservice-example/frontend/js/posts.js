/**
 * Posts Module
 * Handles posts and comments functionality
 */

// Configuration is loaded from config.js

/**
 * Initialize posts module
 */
function initPosts() {
    loadPosts();
}

/**
 * Load all posts
 */
async function loadPosts() {
    try {
        const response = await fetch(POSTS_API_BASE);
        const posts = await response.json();
        displayPosts(posts);
    } catch (error) {
        console.error('Error loading posts:', error);
        alert('Failed to load posts');
    }
}

/**
 * Display posts
 */
function displayPosts(posts) {
    const container = document.getElementById('postsContainer');
    container.innerHTML = '';

    if (posts.length === 0) {
        container.innerHTML = '<p class="text-muted">No posts yet. Be the first to create one!</p>';
        return;
    }

    // Sort posts by date, newest first
    const sortedPosts = [...posts].sort((a, b) => new Date(b.date) - new Date(a.date));

    sortedPosts.forEach(post => {
        const postCard = createPostCard(post);
        container.appendChild(postCard);
    });

    // Update edit button visibility based on auth state
    if (typeof updateAuthUI === 'function') {
        updateAuthUI();
    }
}

/**
 * Create post card element
 */
function createPostCard(post) {
    const card = document.createElement('div');
    card.className = 'card post-card';

    const date = new Date(post.date).toLocaleString();

    card.innerHTML = `
        <div class="card-body">
            <div class="d-flex justify-content-between align-items-start">
                <div class="flex-grow-1">
                    <h5 class="card-title">${escapeHtml(post.title)}</h5>
                    <p class="card-text">${escapeHtml(post.content)}</p>
                    <p class="text-muted small">${date}</p>
                </div>
                <button class="btn btn-sm btn-outline-primary"
                        onclick="showEditModal('${post.id}', \`${escapeHtml(post.title).replace(/`/g, '\\`')}\`, \`${escapeHtml(post.content).replace(/`/g, '\\`')}\`)"
                        style="display: none;"
                        id="editBtn-${post.id}"
                        data-userref="${post.userRef || ''}">
                    Edit
                </button>
            </div>

            <hr>

            <div class="d-flex justify-content-between align-items-center mb-2">
                <h6 class="mb-0">Comments</h6>
                <button class="btn btn-sm btn-outline-secondary" id="loadCommentsBtn-${post.id}"
                        onclick="loadComments('${post.id}')">
                    Load Comments
                </button>
            </div>

            <div class="comment-form">
                <div class="input-group">
                    <input type="text" class="form-control" id="commentText-${post.id}"
                           placeholder="Add a comment...">
                    <button class="btn btn-outline-primary" onclick="addComment('${post.id}')">
                        Post Comment
                    </button>
                </div>
            </div>

            <div id="comments-section-${post.id}" class="comments-section">
                <div id="comments-${post.id}"></div>
            </div>
        </div>
    `;

    return card;
}

/**
 * Show add post modal (with auth check)
 */
function showAddPostModal() {
    requireAuth(() => {
        const modal = new bootstrap.Modal(document.getElementById('addPostModal'));
        modal.show();
    });
}

/**
 * Create new post
 */
async function createPost() {
    const title = document.getElementById('postTitle').value.trim();
    const content = document.getElementById('postContent').value.trim();

    if (!title || !content) {
        alert('Please fill in all fields');
        return;
    }

    try {
        const options = addAuthHeader({
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ title, content })
        });

        const response = await fetch(POSTS_API_BASE, options);

        if (response.ok) {
            // Close modal
            const modal = bootstrap.Modal.getInstance(document.getElementById('addPostModal'));
            modal.hide();

            // Clear form
            document.getElementById('addPostForm').reset();

            // Reload posts
            loadPosts();
        } else {
            alert('Failed to create post');
        }
    } catch (error) {
        console.error('Error creating post:', error);
        alert('Failed to create post');
    }
}

/**
 * Load comments for a post
 */
async function loadComments(postId) {
    const button = document.getElementById(`loadCommentsBtn-${postId}`);
    const section = document.getElementById(`comments-section-${postId}`);

    // If already loaded, just toggle visibility
    if (section.classList.contains('loaded')) {
        section.style.display = section.style.display === 'none' ? 'block' : 'none';
        button.textContent = section.style.display === 'none' ? 'Load Comments' : 'Hide Comments';
        return;
    }

    try {
        button.disabled = true;
        button.textContent = 'Loading...';

        const response = await fetch(`${POSTS_API_BASE}/${postId}/comments`);
        const comments = await response.json();

        displayComments(postId, comments);
        section.classList.add('loaded');
        section.style.display = 'block';

        button.textContent = 'Hide Comments';
        button.disabled = false;
    } catch (error) {
        console.error('Error loading comments:', error);
        button.textContent = 'Load Comments';
        button.disabled = false;
        alert('Failed to load comments');
    }
}

/**
 * Display comments
 */
function displayComments(postId, comments) {
    const container = document.getElementById(`comments-${postId}`);

    if (comments.length === 0) {
        container.innerHTML = '<p class="text-muted small">No comments yet</p>';
        return;
    }

    // Sort comments by date, newest first
    const sortedComments = [...comments].sort((a, b) => new Date(b.date) - new Date(a.date));

    container.innerHTML = '';
    sortedComments.forEach(comment => {
        const commentDiv = document.createElement('div');
        commentDiv.className = 'comment';
        const date = new Date(comment.date).toLocaleString();
        commentDiv.innerHTML = `
            <span class="text-muted small">${date}</span>
            <p class="mb-0">${escapeHtml(comment.text)}</p>
        `;
        container.appendChild(commentDiv);
    });
}

/**
 * Add comment to a post (with auth check)
 */
function addComment(postId) {
    requireAuth(() => {
        submitComment(postId);
    });
}

/**
 * Submit comment to a post
 */
async function submitComment(postId) {
    const text = document.getElementById(`commentText-${postId}`).value.trim();

    if (!text) {
        alert('Please enter a comment');
        return;
    }

    try {
        const options = addAuthHeader({
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                text,
                date: new Date().toISOString()
            })
        });

        const response = await fetch(`${POSTS_API_BASE}/${postId}/comments`, options);

        if (response.ok) {
            // Clear input
            document.getElementById(`commentText-${postId}`).value = '';

            // Make sure comments section is visible
            const section = document.getElementById(`comments-section-${postId}`);
            if (!section.classList.contains('loaded')) {
                section.classList.add('loaded');
                section.style.display = 'block';
                document.getElementById(`loadCommentsBtn-${postId}`).textContent = 'Hide Comments';
            }

            // Reload comments
            await reloadCommentsData(postId);
        } else {
            alert('Failed to add comment');
        }
    } catch (error) {
        console.error('Error adding comment:', error);
        alert('Failed to add comment');
    }
}

/**
 * Reload comments data without toggling visibility
 */
async function reloadCommentsData(postId) {
    try {
        const response = await fetch(`${POSTS_API_BASE}/${postId}/comments`);
        const comments = await response.json();
        displayComments(postId, comments);
    } catch (error) {
        console.error('Error reloading comments:', error);
    }
}

/**
 * Show edit post modal (with auth check)
 */
function showEditModal(postId, title, content) {
    requireAuth(() => {
        document.getElementById('editPostId').value = postId;
        document.getElementById('editPostTitle').value = title;
        document.getElementById('editPostContent').value = content;
        const modal = new bootstrap.Modal(document.getElementById('editPostModal'));
        modal.show();
    });
}

/**
 * Update post
 */
async function updatePost() {
    const postId = document.getElementById('editPostId').value;
    const title = document.getElementById('editPostTitle').value.trim();
    const content = document.getElementById('editPostContent').value.trim();

    if (!title || !content) {
        alert('Please fill in all fields');
        return;
    }

    try {
        const options = addAuthHeader({
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ title, content })
        });

        const response = await fetch(`${POSTS_API_BASE}/${postId}`, options);

        if (response.ok) {
            // Close modal
            const modal = bootstrap.Modal.getInstance(document.getElementById('editPostModal'));
            modal.hide();

            // Clear form
            document.getElementById('editPostForm').reset();

            // Reload posts
            loadPosts();
        } else if (response.status === 403) {
            alert('You can only edit your own posts');
        } else if (response.status === 401) {
            alert('Please log in to edit posts');
        } else {
            alert('Failed to update post');
        }
    } catch (error) {
        console.error('Error updating post:', error);
        alert('Failed to update post');
    }
}

/**
 * Delete post
 */
async function deletePost() {
    const postId = document.getElementById('editPostId').value;

    if (!confirm('Are you sure you want to delete this post? This action cannot be undone.')) {
        return;
    }

    try {
        const options = addAuthHeader({
            method: 'DELETE'
        });

        const response = await fetch(`${POSTS_API_BASE}/${postId}`, options);

        if (response.ok || response.status === 204) {
            // Close modal
            const modal = bootstrap.Modal.getInstance(document.getElementById('editPostModal'));
            modal.hide();

            // Clear form
            document.getElementById('editPostForm').reset();

            // Reload posts
            loadPosts();
        } else if (response.status === 403) {
            alert('You can only delete your own posts');
        } else if (response.status === 401) {
            alert('Please log in to delete posts');
        } else {
            alert('Failed to delete post');
        }
    } catch (error) {
        console.error('Error deleting post:', error);
        alert('Failed to delete post');
    }
}

/**
 * Helper function to escape HTML
 */
function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

// Initialize when DOM is ready
document.addEventListener('DOMContentLoaded', initPosts);
