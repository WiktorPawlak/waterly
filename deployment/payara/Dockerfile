# Base image with Payara Server
FROM payara/server-full:6.2023.3-jdk17

ARG db_server_address
ARG db_admin_pass
ARG db_auth_pass
ARG db_mok_pass
ARG db_mol_pass

ENV DB_URL=jdbc:postgresql://${db_server_address}:5432/ssbd06
ENV DB_ADMIN_PASSWORD=${db_admin_pass}
ENV DB_AUTH_PASSWORD=${db_auth_pass}
ENV DB_MOK_PASSWORD=${db_mok_pass}
ENV DB_MOL_PASSWORD=${db_mol_pass}

ENV SSBD_ENVIRONMENT=PROD

# Expose port 8080
EXPOSE 8080
