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
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE list_search_preferences TO ssbd06mok;
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE two_factor_authentication TO ssbd06mok;

GRANT USAGE, SELECT, UPDATE ON SEQUENCE account_details_id_seq TO ssbd06mok;
GRANT USAGE, SELECT, UPDATE ON SEQUENCE account_id_seq TO ssbd06mok;
GRANT USAGE, SELECT, UPDATE ON SEQUENCE auth_info_id_seq TO ssbd06mok;
GRANT USAGE, SELECT, UPDATE ON SEQUENCE role_id_seq TO ssbd06mok;
GRANT USAGE, SELECT, UPDATE ON SEQUENCE verification_token_id_seq TO ssbd06mok;
GRANT USAGE, SELECT, UPDATE ON SEQUENCE list_search_preferences_id_seq TO ssbd06mok;
GRANT USAGE, SELECT, UPDATE ON SEQUENCE two_factor_authentication_id_seq TO ssbd06mok;


--ssbd06mol
GRANT SELECT ON TABLE account TO ssbd06mol;
GRANT SELECT ON TABLE role TO ssbd06mol;
GRANT SELECT ON TABLE account_details TO ssbd06mol;
GRANT SELECT ON TABLE auth_info to ssbd06mol;
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
INSERT INTO public.account_details (id, version, email, first_name, last_name, phone_number, created_on, updated_on) VALUES (nextval('account_details_id_seq'), 0, 'kontomat@gmail.com', 'Mateusz', 'Strzelecki', '123456789', now(), now());
INSERT INTO public.account (id, version, active, login, password, locale, account_state, account_details_id, created_on, updated_on, two_factor_enabled, otp_secret) VALUES (nextval('account_id_seq'), 0, true, 'admin', '$2a$04$m9vbbL2RTbV/XNC44TEZ0e.t9WH2Q6hjtyEem/siFCdNS564hW68q', 'en_US', 'CONFIRMED', 1, now(), now(), false, '540d1468-f5bf-11ed-b67e-0242ac120002');
INSERT INTO public.role (permission_level, id, active, version, account_id, created_on, updated_on) VALUES ('ADMINISTRATOR', nextval('role_id_seq'), true, 0, 1, now(), now());
INSERT INTO public.role (permission_level, id, active, version, account_id, created_on, updated_on) VALUES ('FACILITY_MANAGER', nextval('role_id_seq'), true, 0, 1, now(), now());
INSERT INTO public.role (permission_level, id, active, version, account_id, created_on, updated_on) VALUES ('OWNER', nextval('role_id_seq'), true, 0, 1, now(), now());
INSERT INTO public.administrator (id) VALUES (1);
INSERT INTO public.facility_manager (id) VALUES (2);
INSERT INTO public.owner (id) VALUES (3);
INSERT INTO public.auth_info (id, last_ip_address, last_success_auth, last_incorrect_auth, incorrect_auth_count, created_on, updated_on, version, account_id) VALUES (nextval('auth_info_id_seq'), null, null, null, 0, now(), now(), 0, 1);
-- add test OWNER password jantes123
INSERT INTO public.account_details (id, version, email, first_name, last_name, phone_number, created_on, updated_on) VALUES (nextval('account_details_id_seq'), 0, 'kontosz@gmail.com', 'Szymon', 'Ziemecki', '123412341', now(), now());
INSERT INTO public.account (id, version, active, login, password, locale, account_state, account_details_id, created_on, updated_on, two_factor_enabled, otp_secret) VALUES (nextval('account_id_seq'), 0, true, 'new', '$2a$04$j/yqCtlHxKmdxHMWxaji4OD1w591LIMNDGBqUbCpD6HTM4aj2uLiS', 'en_US', 'CONFIRMED', 2, now(), now(), false, '59f5bee8-f5bf-11ed-b67e-0242ac120002');
INSERT INTO public.role (permission_level, id, active, version, account_id, created_on, updated_on) VALUES ('OWNER', nextval('role_id_seq'), true, 0, 2, now(), now());
INSERT INTO public.role (permission_level, id, active, version, account_id, created_on, updated_on) VALUES ('FACILITY_MANAGER', nextval('role_id_seq'), false, 0, 2, now(), now());
INSERT INTO public.role (permission_level, id, active, version, account_id, created_on, updated_on) VALUES ('ADMINISTRATOR', nextval('role_id_seq'), false, 0, 2, now(), now());
INSERT INTO public.owner (id) VALUES (4);
INSERT INTO public.facility_manager (id) VALUES (5);
INSERT INTO public.administrator (id) VALUES (6);
INSERT INTO public.auth_info (id, last_ip_address, last_success_auth, last_incorrect_auth, incorrect_auth_count, created_on, updated_on, version, account_id) VALUES (nextval('auth_info_id_seq'), null, null, null, 0, now(), now(), 0, 2);
-- add test FACILITY MANGER password jantes123
INSERT INTO public.account_details (id, version, email, first_name, last_name, phone_number, created_on, updated_on) VALUES (nextval('account_details_id_seq'), 0, 'tomdut@gmail.com', 'Tom', 'Dut', '666666666', now(), now());
INSERT INTO public.account (id, version, active, login, password, locale, account_state, account_details_id, created_on, updated_on, two_factor_enabled, otp_secret) VALUES (nextval('account_id_seq'), 0, true, 'tomdut', '$2a$04$j/yqCtlHxKmdxHMWxaji4OD1w591LIMNDGBqUbCpD6HTM4aj2uLiS', 'en_US', 'CONFIRMED', 3, now(), now(), false, '610cc564-f5bf-11ed-b67e-0242ac120002');
INSERT INTO public.role (permission_level, id, active, version, account_id, created_on, updated_on) VALUES ('FACILITY_MANAGER', nextval('role_id_seq'), true, 0, 3, now(), now());
INSERT INTO public.role (permission_level, id, active, version, account_id, created_on, updated_on) VALUES ('OWNER', nextval('role_id_seq'), false, 0, 3, now(), now());
INSERT INTO public.role (permission_level, id, active, version, account_id, created_on, updated_on) VALUES ('ADMINISTRATOR', nextval('role_id_seq'), false, 0, 3, now(), now());
INSERT INTO public.facility_manager (id) VALUES (7);
INSERT INTO public.owner (id) VALUES (8);
INSERT INTO public.administrator (id) VALUES (9);
INSERT INTO public.auth_info (id, last_ip_address, last_success_auth, last_incorrect_auth, incorrect_auth_count, created_on, updated_on, version, account_id) VALUES (nextval('auth_info_id_seq'), null, null, null, 0, now(), now(), 0, 3);
-- add MANAGER OWNER password jantes123
INSERT INTO public.account_details (id, version, email, first_name, last_name, phone_number, created_on, updated_on) VALUES (nextval('account_details_id_seq'), 0, 'w.tutkaj@gmail.com', 'Weronika', 'Tutkaj', '123462341', now(), now());
INSERT INTO public.account (id, version, active, login, password, locale, account_state, account_details_id, created_on, updated_on, two_factor_enabled, otp_secret) VALUES (nextval('account_id_seq'), 0, true, 'managerowner', '$2a$04$j/yqCtlHxKmdxHMWxaji4OD1w591LIMNDGBqUbCpD6HTM4aj2uLiS', 'en_US', 'CONFIRMED', 4, now(), now(), false, '59f5bee8-f5bf-11ed-b67e-0242ac120002');
INSERT INTO public.role (permission_level, id, active, version, account_id, created_on, updated_on) VALUES ('OWNER', nextval('role_id_seq'), true, 0, 4, now(), now());
INSERT INTO public.role (permission_level, id, active, version, account_id, created_on, updated_on) VALUES ('FACILITY_MANAGER', nextval('role_id_seq'), false, 0, 4, now(), now());
INSERT INTO public.role (permission_level, id, active, version, account_id, created_on, updated_on) VALUES ('ADMINISTRATOR', nextval('role_id_seq'), false, 0, 4, now(), now());
INSERT INTO public.owner (id) VALUES (10);
INSERT INTO public.facility_manager (id) VALUES (11);
INSERT INTO public.administrator (id) VALUES (12);
INSERT INTO public.auth_info (id, last_ip_address, last_success_auth, last_incorrect_auth, incorrect_auth_count, created_on, updated_on, version, account_id) VALUES (nextval('auth_info_id_seq'), null, null, null, 0, now(), now(), 0, 4);
-- add test OWNER password jantes123
INSERT INTO public.account_details (id, version, email, first_name, last_name, phone_number, created_on, updated_on) VALUES (nextval('account_details_id_seq'), 0, 'nikat2001@gmail.com', 'Weronika', 'Testowa', '123462387', now(), now());
INSERT INTO public.account (id, version, active, login, password, locale, account_state, account_details_id, created_on, updated_on, two_factor_enabled, otp_secret) VALUES (nextval('account_id_seq'), 0, true, 'nikattoja', '$2a$04$j/yqCtlHxKmdxHMWxaji4OD1w591LIMNDGBqUbCpD6HTM4aj2uLiS', 'en_US', 'CONFIRMED', 5, now(), now(), false, '59f5bee8-f5bf-11ed-b67e-0242ac120002');
INSERT INTO public.role (permission_level, id, active, version, account_id, created_on, updated_on) VALUES ('OWNER', nextval('role_id_seq'), true, 0, 5, now(), now());
INSERT INTO public.role (permission_level, id, active, version, account_id, created_on, updated_on) VALUES ('FACILITY_MANAGER', nextval('role_id_seq'), false, 0, 5, now(), now());
INSERT INTO public.role (permission_level, id, active, version, account_id, created_on, updated_on) VALUES ('ADMINISTRATOR', nextval('role_id_seq'), false, 0, 5, now(), now());
INSERT INTO public.owner (id) VALUES (13);
INSERT INTO public.facility_manager (id) VALUES (14);
INSERT INTO public.administrator (id) VALUES (15);
INSERT INTO public.auth_info (id, last_ip_address, last_success_auth, last_incorrect_auth, incorrect_auth_count, created_on, updated_on, version, account_id) VALUES (nextval('auth_info_id_seq'), null, null, null, 0, now(), now(), 0, 5);
-- mol
INSERT INTO public.invoice(id, version, created_on, created_by, updated_on, updated_by, invoice_number, water_usage, total_cost, date) values (nextval('invoice_id_seq'), 0, now(), null, now(), null, 'FV 2020/01/23', 29.09, 202.57, '2023-04-1');
INSERT INTO public.invoice(id, version, created_on, created_by, updated_on, updated_by, invoice_number, water_usage, total_cost, date) values (nextval('invoice_id_seq'), 1, now(), null, now(), null, 'FV 2020/02/23', 30.09, 222.57, '2023-05-1');
INSERT INTO public.tariff(id, version, created_on, created_by, updated_on, updated_by, cold_water_price, hot_water_price, trash_price, start_date, end_date) values (nextval('tariff_id_seq'), 0, now(), null, now(), null, 9.81, 9.02, 2.22, '2023-01-01', '2023-05-31');
INSERT INTO public.tariff(id, version, created_on, created_by, updated_on, updated_by, cold_water_price, hot_water_price, trash_price, start_date, end_date) values (nextval('tariff_id_seq'), 0, now(), null, now(), null, 9.81, 9.02, 2.22, '2023-02-01', '2023-03-31');
INSERT INTO public.tariff(id, version, created_on, created_by, updated_on, updated_by, cold_water_price, hot_water_price, trash_price, start_date, end_date) values (nextval('tariff_id_seq'), 0, now(), null, now(), null, 9.81, 9.02, 2.22, '2023-06-01', '2024-08-30');
INSERT INTO public.apartment (id, version, number, created_on, updated_on, area, owner_id) VALUES (nextval('apartment_id_seq'), 0, '12a', now(), now(), 40.00, 2);
INSERT INTO public.apartment (id, version, number, created_on, updated_on, area, owner_id) VALUES (nextval('apartment_id_seq'), 0, '11a', now(), now(), 50.00, 1);
INSERT INTO public.water_meter (id, version, active, expiry_date, starting_value, expected_daily_usage, type, apartment_id, created_on, updated_on) VALUES (nextval('water_meter_id_seq'), 0, true, now() + INTERVAL '360 days', 100.000, 0.500, 'HOT_WATER', 1, now(), now());
INSERT INTO public.water_meter (id, version, active, expiry_date, starting_value, expected_daily_usage, type, apartment_id, created_on, updated_on) VALUES (nextval('water_meter_id_seq'), 0, true, now() + INTERVAL '360 days', 100.000, 0.600, 'COLD_WATER', 1, now(), now());
INSERT INTO public.water_meter (id, version, active, expiry_date, starting_value, expected_daily_usage, type, apartment_id, created_on, updated_on) VALUES (nextval('water_meter_id_seq'), 0, true, now() + INTERVAL '360 days', 100.000, 0.700, 'COLD_WATER', 1, now(), now());
INSERT INTO public.water_meter (id, version, active, expiry_date, starting_value, expected_daily_usage, type, apartment_id, created_on, updated_on) VALUES (nextval('water_meter_id_seq'), 0, true, now() + INTERVAL '360 days', 100.000, 0.200, 'HOT_WATER', 2, now(), now());
INSERT INTO public.water_meter (id, version, active, expiry_date, starting_value, expected_daily_usage, type, apartment_id, created_on, updated_on) VALUES (nextval('water_meter_id_seq'), 0, true, now() + INTERVAL '360 days', 100.000, 0.300, 'COLD_WATER', 2, now(), now());
INSERT INTO public.usage_report (id, created_on, updated_on, version, cold_water_cost, cold_water__usage, garbage_cost, hot_water_cost, hot_water_usage, unbilled_water_amount, unbilled_water_cost) VALUES (nextval('usage_report_id_seq'), now(), now(), 0, 10, 10, 10, 10, 10, 10, 10);
INSERT INTO public.usage_report (id, created_on, updated_on, version, cold_water_cost, cold_water__usage, garbage_cost, hot_water_cost, hot_water_usage, unbilled_water_amount, unbilled_water_cost) VALUES (nextval('usage_report_id_seq'), now(), now(), 0, 11, 12, 13, 14, 15, 16, 17);
INSERT INTO public.bill (id, version, created_on, updated_on, balance, date, advance_usage, apartment_id, owner_id, real_usage) VALUES (nextval('bill_id_seq'), 0, now(), now(), 9.02, '2023-01-01', 1, 1, 2, 2);
INSERT INTO public.water_usage_stats (id, created_on, updated_on, version, cold_water_usage, hot_water_usage, year_month, apartment_id) VALUES (nextval('water_usage_stats_id_seq'), now(), now(), 0, 40.000, 60.000, '2023-03-01', 1);
INSERT INTO public.water_usage_stats (id, created_on, updated_on, version, cold_water_usage, hot_water_usage, year_month, apartment_id) VALUES (nextval('water_usage_stats_id_seq'), now(), now(), 0, 40.000, 60.000, '2023-03-01', 2);