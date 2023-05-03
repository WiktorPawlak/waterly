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

GRANT USAGE, SELECT, UPDATE ON SEQUENCE account_details_id_seq TO ssbd06mok;
GRANT USAGE, SELECT, UPDATE ON SEQUENCE account_id_seq TO ssbd06mok;
GRANT USAGE, SELECT, UPDATE ON SEQUENCE auth_info_id_seq TO ssbd06mok;
GRANT USAGE, SELECT, UPDATE ON SEQUENCE role_id_seq TO ssbd06mok;
GRANT USAGE, SELECT, UPDATE ON SEQUENCE verification_token_id_seq TO ssbd06mok;

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

GRANT USAGE, SELECT, UPDATE ON SEQUENCE apartment_id_seq TO ssbd06mol;
GRANT USAGE, SELECT, UPDATE ON SEQUENCE bill_id_seq TO ssbd06mol;
GRANT USAGE, SELECT, UPDATE ON SEQUENCE invoice_id_seq TO ssbd06mol;
GRANT USAGE, SELECT, UPDATE ON SEQUENCE tariff_id_seq TO ssbd06mol;
GRANT USAGE, SELECT, UPDATE ON SEQUENCE usage_report_id_seq TO ssbd06mol;
GRANT USAGE, SELECT, UPDATE ON SEQUENCE water_meter_check_id_seq TO ssbd06mol;
GRANT USAGE, SELECT, UPDATE ON SEQUENCE water_meter_id_seq TO ssbd06mol;
GRANT USAGE, SELECT, UPDATE ON SEQUENCE water_usage_stats_id_seq TO ssbd06mol;

-- add first administrator password admin12345
INSERT INTO public.account_details (id, version, email, first_name, last_name, phone_number, created_on, updated_on) VALUES (nextval('account_details_id_seq'), 0, 'kontomatino@gmail.com', 'Mateusz', 'Strzelecki', '123456789', now(), now());
INSERT INTO public.account (id, version, active, login, password, locale, account_state, account_details_id, created_on, updated_on) VALUES (nextval('account_id_seq'), 0, true, 'admin', '$2a$04$m9vbbL2RTbV/XNC44TEZ0e.t9WH2Q6hjtyEem/siFCdNS564hW68q', 'en_US', 'CONFIRMED', 1, now(), now());
INSERT INTO public.role (permission_level, id, active, version, account_id, created_on, updated_on) VALUES ('ADMINISTRATOR', nextval('role_id_seq'), true, 0, 1, now(), now());
INSERT INTO public.role (permission_level, id, active, version, account_id, created_on, updated_on) VALUES ('FACILITY_MANAGER', nextval('role_id_seq'), true, 0, 1, now(), now());
INSERT INTO public.role (permission_level, id, active, version, account_id, created_on, updated_on) VALUES ('OWNER', nextval('role_id_seq'), true, 0, 1, now(), now());
INSERT INTO public.administrator (id) VALUES (1);
INSERT INTO public.facility_manager (id) VALUES (2);
INSERT INTO public.owner (id) VALUES (3);
INSERT INTO public.auth_info (id, last_ip_address, last_success_auth, last_incorrect_auth, incorrect_auth_count, created_on, updated_on, version, account_id) VALUES (nextval('auth_info_id_seq'), null, null, null, 0, now(), now(), 0, 1);
-- add test OWNER password jantes123
INSERT INTO public.account_details (id, version, email, first_name, last_name, phone_number, created_on, updated_on) VALUES (nextval('account_details_id_seq'), 0, 'kontosz@gmail.com', 'Szymon', 'Ziemecki', '123412341', now(), now());
INSERT INTO public.account (id, version, active, login, password, locale, account_state, account_details_id, created_on, updated_on) VALUES (nextval('account_id_seq'), 0, true, 'new', '$2a$04$j/yqCtlHxKmdxHMWxaji4OD1w591LIMNDGBqUbCpD6HTM4aj2uLiS', 'en_US', 'CONFIRMED', 2, now(), now());
INSERT INTO public.role (permission_level, id, active, version, account_id, created_on, updated_on) VALUES ('OWNER', nextval('role_id_seq'), true, 0, 2, now(), now());
INSERT INTO public.role (permission_level, id, active, version, account_id, created_on, updated_on) VALUES ('FACILITY_MANAGER', nextval('role_id_seq'), false, 0, 2, now(), now());
INSERT INTO public.role (permission_level, id, active, version, account_id, created_on, updated_on) VALUES ('ADMINISTRATOR', nextval('role_id_seq'), false, 0, 2, now(), now());
INSERT INTO public.owner (id) VALUES (4);
INSERT INTO public.administrator (id) VALUES (5);
INSERT INTO public.facility_manager (id) VALUES (6);
INSERT INTO public.auth_info (id, last_ip_address, last_success_auth, last_incorrect_auth, incorrect_auth_count, created_on, updated_on, version, account_id) VALUES (nextval('auth_info_id_seq'), null, null, null, 0, now(), now(), 0, 2);
-- add test FACILITY MANGER password jantes123
INSERT INTO public.account_details (id, version, email, first_name, last_name, phone_number, created_on, updated_on) VALUES (nextval('account_details_id_seq'), 0, 'tomdut@gmail.com', 'Tom', 'Dut', '666666666', now(), now());
INSERT INTO public.account (id, version, active, login, password, locale, account_state, account_details_id, created_on, updated_on) VALUES (nextval('account_id_seq'), 0, true, 'tomdut', '$2a$04$j/yqCtlHxKmdxHMWxaji4OD1w591LIMNDGBqUbCpD6HTM4aj2uLiS', 'en_US', 'CONFIRMED', 3, now(), now());
INSERT INTO public.role (permission_level, id, active, version, account_id, created_on, updated_on) VALUES ('FACILITY_MANAGER', nextval('role_id_seq'), true, 0, 3, now(), now());
INSERT INTO public.role (permission_level, id, active, version, account_id, created_on, updated_on) VALUES ('OWNER', nextval('role_id_seq'), false, 0, 3, now(), now());
INSERT INTO public.role (permission_level, id, active, version, account_id, created_on, updated_on) VALUES ('ADMINISTRATOR', nextval('role_id_seq'), false, 0, 3, now(), now());
INSERT INTO public.facility_manager (id) VALUES (7);
INSERT INTO public.owner (id) VALUES (8);
INSERT INTO public.administrator (id) VALUES (9);
INSERT INTO public.auth_info (id, last_ip_address, last_success_auth, last_incorrect_auth, incorrect_auth_count, created_on, updated_on, version, account_id) VALUES (nextval('auth_info_id_seq'), null, null, null, 0, now(), now(), 0, 3);