#!/bin/bash

# Stop Nginx if it's already running
docker stop nginx-container || true

# Remove the existing Nginx container if it exists
docker rm nginx-container || true

# Run the Nginx container
docker run -d --name nginx-container -p 80:80 -v /var/lib/jenkins/workspace/SSBD202306/frontend/dist:/usr/share/nginx/html nginx-img
