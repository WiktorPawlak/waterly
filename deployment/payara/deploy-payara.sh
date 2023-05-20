#!/bin/bash

# Stop the Payara container if it's already running
docker stop payara-container || true

# Remove the existing Payara container if it exists
docker rm payara-container || true

# Run the Payara container and deploy the WAR file
docker run -d --name payara-container -p 9191:8181 -v /var/lib/jenkins/workspace/SSBD202306/target/ssbd06.war:/opt/payara/deployments/ssbd06.war payara-img
