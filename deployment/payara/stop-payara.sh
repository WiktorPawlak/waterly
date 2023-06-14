#!/bin/bash

# Stop the Payara container if it's already running
docker stop payara-container || true

# Remove the existing Payara container if it exists
docker rm payara-container || true