--ssbd06admin
GRANT ALL ON SCHEMA public TO ssbd06admin;

--ssbd06auth
GRANT SELECT ON TABLE account TO ssbd06auth;
GRANT SELECT ON TABLE role TO ssbd06auth;
GRANT SELECT ON TABLE auth_info TO ssbd06auth;

GRANT SELECT, INSERT, UPDATE ON TABLE account_details_id_seq TO ssbd06auth;
GRANT SELECT, INSERT, UPDATE ON TABLE account_id_seq TO ssbd06auth;
GRANT SELECT, INSERT, UPDATE ON TABLE auth_info_id_seq TO ssbd06auth;
GRANT SELECT, INSERT, UPDATE ON TABLE role_id_seq TO ssbd06auth;

--ssbd06mok
GRANT SELECT, INSERT, UPDATE ON TABLE account TO ssbd06mok;
GRANT SELECT, INSERT, UPDATE ON TABLE account_details TO ssbd06mok;
GRANT SELECT, INSERT, UPDATE ON TABLE administrator TO ssbd06mok;
GRANT SELECT, INSERT, UPDATE ON TABLE owner TO ssbd06mok;
GRANT SELECT, INSERT, UPDATE ON TABLE facility_manager TO ssbd06mok;
GRANT SELECT, INSERT, UPDATE ON TABLE role TO ssbd06mok;
GRANT SELECT, INSERT, UPDATE ON TABLE auth_info TO ssbd06mok;

GRANT SELECT, INSERT, UPDATE ON TABLE account_details_id_seq TO ssbd06mok;
GRANT SELECT, INSERT, UPDATE ON TABLE account_id_seq TO ssbd06mok;
GRANT SELECT, INSERT, UPDATE ON TABLE auth_info_id_seq TO ssbd06mok;
GRANT SELECT, INSERT, UPDATE ON TABLE role_id_seq TO ssbd06mok;

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

-- add first administrator
INSERT INTO public.account_details (id, version, email, first_name, last_name, phone_number, created_on, updated_on) VALUES (nextval('account_details_id_seq'), 0, 'kontomatino@gmail.com', 'Mateusz', 'Strzelecki', '123456789', now(), now());
INSERT INTO public.account (id, version, active, login, password, account_state, account_details_id, created_on, updated_on) VALUES (nextval('account_id_seq'), 0, true, 'admin', 'admin12345', 'CONFIRMED', 1, now(), now());
INSERT INTO public.role (permission_level, id, version, active, account_id, created_on, updated_on) VALUES ('ADMINISTRATOR', nextval('role_id_seq'), 0, true, 1, now(), now());
INSERT INTO public.administrator (id) VALUES (1);
INSERT INTO public.auth_info (id, last_ip_address, last_success_auth, last_incorrect_auth, incorrect_auth_count, created_on, updated_on, version, account_id) VALUES (nextval('auth_info_id_seq'), null, null, null, 0, now(), now(), 0, 1);

INSERT INTO public.account_details (id, version, email, first_name, last_name, phone_number, created_on, updated_on) VALUES (nextval('account_details_id_seq'), 0, 'kontosz@gmail.com', 'Szymon', 'Ziemecki', '123456789', now(), now());
INSERT INTO public.account (id, version, active, login, password, account_state, account_details_id, created_on, updated_on) VALUES (nextval('account_id_seq'), 0, true, 'new', 'new12345', 'CONFIRMED', 2, now(), now());
INSERT INTO public.role (permission_level, id, version, active, account_id, created_on, updated_on) VALUES ('OWNER', nextval('role_id_seq'), 0, true, 2, now(), now());
INSERT INTO public.owner (id) VALUES (2);
INSERT INTO public.auth_info (id, last_ip_address, last_success_auth, last_incorrect_auth, incorrect_auth_count, created_on, updated_on, version, account_id) VALUES (nextval('auth_info_id_seq'), null, null, null, 0, now(), now(), 0, 2);