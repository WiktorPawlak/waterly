--ssbd06admin
GRANT ALL ON SCHEMA public TO ssbd06admin;

--ssbd06auth
GRANT SELECT ON TABLE account TO ssbd06auth;
GRANT SELECT ON TABLE role TO ssbd06auth;
GRANT SELECT ON TABLE auth_info TO ssbd06auth;

--ssbd06mok
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE account TO ssbd06mok;
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE account_details TO ssbd06mok;
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE administrator TO ssbd06mok;
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE owner TO ssbd06mok;
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE facility_manager TO ssbd06mok;
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE role TO ssbd06mok;
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE verification_token TO ssbd06mok;
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE auth_info TO ssbd06mok;

GRANT USAGE, SELECT ON SEQUENCE account_details_id_seq TO ssbd06mok;
GRANT USAGE, SELECT ON SEQUENCE account_id_seq TO ssbd06mok;
GRANT USAGE, SELECT ON SEQUENCE auth_info_id_seq TO ssbd06mok;
GRANT USAGE, SELECT ON SEQUENCE role_id_seq TO ssbd06mok;
GRANT USAGE, SELECT ON SEQUENCE verification_token_id_seq TO ssbd06mok;

--ssbd06mol
GRANT SELECT ON TABLE account TO ssbd06mol;
GRANT SELECT ON TABLE role TO ssbd06mol;
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE apartment TO ssbd06mol;
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE bill TO ssbd06mol;
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE invoice TO ssbd06mol;
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE tariff TO ssbd06mol;
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE usage_report TO ssbd06mol;
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE water_meter TO ssbd06mol;
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE water_meter_check TO ssbd06mol;
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE water_usage_stats TO ssbd06mol;

GRANT USAGE, SELECT ON SEQUENCE apartment_id_seq TO ssbd06mol;
GRANT USAGE, SELECT ON SEQUENCE bill_id_seq TO ssbd06mol;
GRANT USAGE, SELECT ON SEQUENCE invoice_id_seq TO ssbd06mol;
GRANT USAGE, SELECT ON SEQUENCE tariff_id_seq TO ssbd06mol;
GRANT USAGE, SELECT ON SEQUENCE usage_report_id_seq TO ssbd06mol;
GRANT USAGE, SELECT ON SEQUENCE water_meter_check_id_seq TO ssbd06mol;
GRANT USAGE, SELECT ON SEQUENCE water_meter_id_seq TO ssbd06mol;
GRANT USAGE, SELECT ON SEQUENCE water_usage_stats_id_seq TO ssbd06mol;

-- Stworzenie konta administratora - hasło admin12345
INSERT INTO public.account_details (id, version, email, first_name, last_name, phone_number) VALUES (nextval('account_details_id_seq'), 0, 'kontomatino@gmail.com', 'Mateusz', 'Strzelecki', '123456789');
INSERT INTO public.account (id, version, active, login, password, account_state, account_details_id) VALUES (nextval('account_id_seq'), 0, true, 'admin', '$2a$04$m9vbbL2RTbV/XNC44TEZ0e.t9WH2Q6hjtyEem/siFCdNS564hW68q', 'CONFIRMED', 1);
INSERT INTO public.role (permission_level, id, active, version, account_id) VALUES ('ADMINISTRATOR', nextval('role_id_seq'), true, 0, 1);
INSERT INTO public.role (permission_level, id, active, version, account_id) VALUES ('FACILITY_MANAGER', nextval('role_id_seq'), false, 0, 1);
INSERT INTO public.role (permission_level, id, active, version, account_id) VALUES ('OWNER', nextval('role_id_seq'), false, 0, 1);
INSERT INTO public.administrator (id) VALUES (1);
INSERT INTO public.owner (id) VALUES (1);
INSERT INTO public.facility_manager (id) VALUES (1);
-- Stworzenie konta zarządcy - hasło jantes123
INSERT INTO public.account_details (id, version, email, first_name, last_name, phone_number) VALUES (nextval('account_details_id_seq'), 0, 'kontosz@gmail.com', 'Szymon', 'Ziemecki', '123412341');
INSERT INTO public.account (id, version, active, login, password, account_state, account_details_id) VALUES (nextval('account_id_seq'), 0, true, 'new', '$2a$04$j/yqCtlHxKmdxHMWxaji4OD1w591LIMNDGBqUbCpD6HTM4aj2uLiS', 'CONFIRMED', 2);
INSERT INTO public.role (permission_level, id, active, version, account_id) VALUES ('OWNER', nextval('role_id_seq'), false, 0, 2);
INSERT INTO public.role (permission_level, id, active, version, account_id) VALUES ('FACILITY_MANAGER', nextval('role_id_seq'), true, 0, 2);
INSERT INTO public.role (permission_level, id, active, version, account_id) VALUES ('ADMINISTRATOR', nextval('role_id_seq'), false, 0, 2);
INSERT INTO public.administrator (id) VALUES (2);
INSERT INTO public.owner (id) VALUES (2);
INSERT INTO public.facility_manager (id) VALUES (2);
-- Stworzenie konta właściciela lokalu - hasło jantes123
INSERT INTO public.account_details (id, version, email, first_name, last_name, phone_number) VALUES (nextval('account_details_id_seq'), 0, 'tomdut@gmail.com', 'Tom', 'Dut', '666666666');
INSERT INTO public.account (id, version, active, login, password, account_state, account_details_id) VALUES (nextval('account_id_seq'), 0, true, 'tomdut', '$2a$04$j/yqCtlHxKmdxHMWxaji4OD1w591LIMNDGBqUbCpD6HTM4aj2uLiS', 'CONFIRMED', 3);
INSERT INTO public.role (permission_level, id, active, version, account_id) VALUES ('OWNER', nextval('role_id_seq'), true, 0, 3);
INSERT INTO public.role (permission_level, id, active, version, account_id) VALUES ('FACILITY_MANAGER', nextval('role_id_seq'), false, 0, 3);
INSERT INTO public.role (permission_level, id, active, version, account_id) VALUES ('ADMINISTRATOR', nextval('role_id_seq'), false, 0, 3);
INSERT INTO public.administrator (id) VALUES (3);
INSERT INTO public.owner (id) VALUES (3);
INSERT INTO public.facility_manager (id) VALUES (3);