FROM maven:3.8.3-openjdk-17-slim AS build-stage-backend

WORKDIR /app-backend

COPY pom.xml .

COPY src ./src

RUN mvn package -DskipTests

FROM payara/server-full:6.2023.3-jdk17 as payara-build

ENV DEPLOY_DIR=/opt/payara/deployments

ARG db_server_address
ARG db_admin_pass
ARG db_auth_pass
ARG db_mok_pass
ARG db_mol_pass
ARG app_name

ENV DB_URL=jdbc:postgresql://${db_server_address}:5432/ssbd06
ENV DB_ADMIN_PASSWORD=${db_admin_pass}
ENV DB_AUTH_PASSWORD=${db_auth_pass}
ENV DB_MOK_PASSWORD=${db_mok_pass}
ENV DB_MOL_PASSWORD=${db_mol_pass}
ENV APP_WAR_TARGET_FOLDER=/app-backend/target/${app_name}.war
ENV APP_DEPLOYMENT_FOLDER=/opt/payara/deployments/${app_name}.war
ENV SSBD_ENVIRONMENT=PROD

COPY --from=build-stage-backend ${APP_WAR_TARGET_FOLDER} ${APP_DEPLOYMENT_FOLDER}

EXPOSE 8181
