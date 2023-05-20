# Base image with Nginx
FROM nginx:latest

# Remove default Nginx configuration
RUN rm /etc/nginx/conf.d/default.conf

# Copy the default Nginx configuration for serving the SPA
COPY nginx.conf /etc/nginx/conf.d/

# Create a directory to store the SPA
RUN mkdir -p /usr/share/nginx/html

# Expose port 80
EXPOSE 80
