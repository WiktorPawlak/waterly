FROM mariadb:11.1

ENV MARIADB_ROOT_HOST=admin
ENV MARIADB_ROOT_PASSWORD=admin

COPY docker-init.sql /docker-entrypoint-initdb.d/docker-init.sql

EXPOSE 3306