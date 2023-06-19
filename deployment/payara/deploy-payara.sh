#!/bin/bash

# Pull docker image from dockerhub
docker pull matino1/ssbd06:payara-1

# Run the Payara container and deploy the WAR file
docker run -d --name payara-container -p 9090:8080 -v /var/lib/jenkins/workspace/SSBD202306/target/ssbd06.war:/opt/payara/deployments/ssbd06.war matino1/ssbd06:payara-1
