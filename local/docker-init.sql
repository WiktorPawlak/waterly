CREATE USER 'ssbd06admin'@'%' IDENTIFIED BY '12345';
CREATE USER 'ssbd06auth'@'%' IDENTIFIED BY '12345';
CREATE USER 'ssbd06mok'@'%' IDENTIFIED BY '12345';
CREATE USER 'ssbd06mol'@'%' IDENTIFIED BY '12345';

CREATE DATABASE ssbd06;

USE ssbd06;

GRANT ALL PRIVILEGES ON ssbd06.* TO 'ssbd06admin'@'%' WITH GRANT OPTION;