#!/bin/bash

# Build the Docker image for Payara Server
docker build --build-arg db_server_address="${DB_SERVER_ADDRESS}" --build-arg db_admin_pass="${DB_ADMIN_PASS}" --build-arg db_auth_pass="${DB_AUTH_PASS}" --build-arg db_mok_pass="${DB_MOK_PASS}" --build-arg db_mol_pass="${DB_MOL_PASS}" -t payara-img .
