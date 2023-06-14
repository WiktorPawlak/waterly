#!/bin/bash

# Pull docker image from dockerhub
docker pull matino1/ssbd06:nginx-1

# Run the Nginx container
docker run -d --name nginx-container -p 80:80 -v /var/lib/jenkins/workspace/SSBD202306/frontend/dist:/usr/share/nginx/html matino1/ssbd06:nginx-1
