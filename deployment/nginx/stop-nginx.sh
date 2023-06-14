#!/bin/bash

# Stop Nginx if it's already running
docker stop nginx-container || true

# Remove the existing Nginx container if it exists
docker rm nginx-container || true