FROM nginx

# Copy static files
COPY index.html /usr/share/nginx/html/
COPY css/ /usr/share/nginx/html/css/
COPY js/ /usr/share/nginx/html/js/

# Copy custom nginx configuration with health endpoint
COPY nginx.conf /etc/nginx/nginx.conf