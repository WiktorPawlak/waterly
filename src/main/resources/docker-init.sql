CREATE USER ssbd06admin WITH ENCRYPTED PASSWORD '12345';
CREATE USER ssbd06auth WITH ENCRYPTED PASSWORD '12345';
CREATE USER ssbd06mok WITH ENCRYPTED PASSWORD '12345';
CREATE USER ssbd06mol WITH ENCRYPTED PASSWORD '12345';

CREATE DATABASE ssbd06;

\c ssbd06

GRANT ALL ON SCHEMA public TO ssbd06admin;