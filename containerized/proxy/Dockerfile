# Base image with Nginx
FROM nginx:latest

# Copy the default Nginx configuration for serving the gateway
COPY nginx.conf /etc/nginx/

# Copy SSL related files
COPY self-signed.crt /etc/ssl/certs/self-signed.crt
COPY self-signed.key /etc/ssl/private/self-signed.key
COPY self-signed.conf /etc/nginx/snippets/self-signed.conf
COPY ssl-params.conf /etc/nginx/snippets/ssl-params.conf
COPY dhparam.pem /etc/ssl/certs/dhparam.pem

# Expose port 80
EXPOSE 80
