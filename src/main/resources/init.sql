#ssbd06admin
GRANT ALL PRIVILEGES ON ssbd06.* TO ssbd06admin;

#ssbd06auth
GRANT SELECT ON account TO ssbd06auth;
GRANT SELECT ON role TO ssbd06auth;
GRANT SELECT ON auth_info TO ssbd06auth;

#ssbd06mok
GRANT SELECT, INSERT, UPDATE, DELETE ON account TO ssbd06mok;
GRANT SELECT, INSERT, UPDATE, DELETE ON account_details TO ssbd06mok;
GRANT SELECT, INSERT, UPDATE, DELETE ON administrator TO ssbd06mok;
GRANT SELECT, INSERT, UPDATE, DELETE ON owner TO ssbd06mok;
GRANT SELECT, INSERT, UPDATE, DELETE ON facility_manager TO ssbd06mok;
GRANT SELECT, INSERT, UPDATE, DELETE ON role TO ssbd06mok;
GRANT SELECT, INSERT, UPDATE, DELETE ON verification_token TO ssbd06mok;
GRANT SELECT, INSERT, UPDATE, DELETE ON auth_info TO ssbd06mok;
GRANT SELECT, INSERT, UPDATE, DELETE ON list_search_preferences TO ssbd06mok;
GRANT SELECT, INSERT, UPDATE, DELETE ON two_factor_authentication TO ssbd06mok;

#ssbd06mol
GRANT SELECT ON account TO ssbd06mol;
GRANT SELECT ON role TO ssbd06mol;
GRANT SELECT ON account_details TO ssbd06mol;
GRANT SELECT ON auth_info to ssbd06mol;
GRANT SELECT, INSERT, UPDATE, DELETE ON apartment TO ssbd06mol;
GRANT SELECT, INSERT, UPDATE, DELETE ON bill TO ssbd06mol;
GRANT SELECT, INSERT, UPDATE, DELETE ON invoice TO ssbd06mol;
GRANT SELECT, INSERT, UPDATE, DELETE ON tariff TO ssbd06mol;
GRANT SELECT, INSERT, UPDATE, DELETE ON usage_report TO ssbd06mol;
GRANT SELECT, INSERT, UPDATE, DELETE ON water_meter TO ssbd06mol;
GRANT SELECT, INSERT, UPDATE, DELETE ON water_meter_check TO ssbd06mol;
GRANT SELECT, INSERT, UPDATE, DELETE ON water_usage_stats TO ssbd06mol;
GRANT SELECT, INSERT, UPDATE, DELETE ON entity_consistence_assurance TO ssbd06mol;

# add first administrator password admin12345
INSERT INTO account_details (version, email, first_name, last_name, phone_number, created_on, updated_on) VALUES (0, 'kontomat@gmail.com', 'Mateusz', 'Strzelecki', '123456789', now(), now());
INSERT INTO account (version, active, login, password, locale, account_state, account_details_id, created_on, updated_on, two_factor_enabled, otp_secret) VALUES (0, true, 'admin', '$2a$04$m9vbbL2RTbV/XNC44TEZ0e.t9WH2Q6hjtyEem/siFCdNS564hW68q', 'en_US', 'CONFIRMED', 1, now(), now(), false, '540d1468-f5bf-11ed-b67e-0242ac120002');
INSERT INTO role (permission_level, active, version, account_id, created_on, updated_on) VALUES ('ADMINISTRATOR', true, 0, 1, now(), now());
INSERT INTO role (permission_level, active, version, account_id, created_on, updated_on) VALUES ('FACILITY_MANAGER', true, 0, 1, now(), now());
INSERT INTO role (permission_level, active, version, account_id, created_on, updated_on) VALUES ('OWNER', true, 0, 1, now(), now());
INSERT INTO administrator (id) VALUES (1);
INSERT INTO facility_manager (id) VALUES (2);
INSERT INTO owner (id) VALUES (3);
INSERT INTO auth_info (last_ip_address, last_success_auth, last_incorrect_auth, incorrect_auth_count, created_on, updated_on, version, account_id) VALUES (null, null, null, 0, now(), now(), 0, 1);
# add test OWNER password jantes123
INSERT INTO account_details (version, email, first_name, last_name, phone_number, created_on, updated_on) VALUES (0, 'kontosz@gmail.com', 'Szymon', 'Ziemecki', '123412341', now(), now());
INSERT INTO account (version, active, login, password, locale, account_state, account_details_id, created_on, updated_on, two_factor_enabled, otp_secret) VALUES (0, true, 'new', '$2a$04$j/yqCtlHxKmdxHMWxaji4OD1w591LIMNDGBqUbCpD6HTM4aj2uLiS', 'en_US', 'CONFIRMED', 2, now(), now(), false, '59f5bee8-f5bf-11ed-b67e-0242ac120002');
INSERT INTO role (permission_level, active, version, account_id, created_on, updated_on) VALUES ('OWNER', true, 0, 2, now(), now());
INSERT INTO role (permission_level, active, version, account_id, created_on, updated_on) VALUES ('FACILITY_MANAGER', false, 0, 2, now(), now());
INSERT INTO role (permission_level, active, version, account_id, created_on, updated_on) VALUES ('ADMINISTRATOR', false, 0, 2, now(), now());
INSERT INTO owner (id) VALUES (4);
INSERT INTO facility_manager (id) VALUES (5);
INSERT INTO administrator (id) VALUES (6);
INSERT INTO auth_info (last_ip_address, last_success_auth, last_incorrect_auth, incorrect_auth_count, created_on, updated_on, version, account_id) VALUES (null, null, null, 0, now(), now(), 0, 2);
# add test FACILITY MANGER password jantes123
INSERT INTO account_details (version, email, first_name, last_name, phone_number, created_on, updated_on) VALUES (0, 'tomdut@gmail.com', 'Tom', 'Dut', '666666666', now(), now());
INSERT INTO account (version, active, login, password, locale, account_state, account_details_id, created_on, updated_on, two_factor_enabled, otp_secret) VALUES (0, true, 'tomdut', '$2a$04$j/yqCtlHxKmdxHMWxaji4OD1w591LIMNDGBqUbCpD6HTM4aj2uLiS', 'en_US', 'CONFIRMED', 3, now(), now(), false, '610cc564-f5bf-11ed-b67e-0242ac120002');
INSERT INTO role (permission_level, active, version, account_id, created_on, updated_on) VALUES ('FACILITY_MANAGER', true, 0, 3, now(), now());
INSERT INTO role (permission_level, active, version, account_id, created_on, updated_on) VALUES ('OWNER', false, 0, 3, now(), now());
INSERT INTO role (permission_level, active, version, account_id, created_on, updated_on) VALUES ('ADMINISTRATOR', false, 0, 3, now(), now());
INSERT INTO facility_manager (id) VALUES (7);
INSERT INTO owner (id) VALUES (8);
INSERT INTO administrator (id) VALUES (9);
INSERT INTO auth_info (last_ip_address, last_success_auth, last_incorrect_auth, incorrect_auth_count, created_on, updated_on, version, account_id) VALUES (null, null, null, 0, now(), now(), 0, 3);
# add MANAGER OWNER password jantes123
INSERT INTO account_details (version, email, first_name, last_name, phone_number, created_on, updated_on) VALUES (0, 'w.tutkaj@gmail.com', 'Weronika', 'Tutkaj', '123462341', now(), now());
INSERT INTO account (version, active, login, password, locale, account_state, account_details_id, created_on, updated_on, two_factor_enabled, otp_secret) VALUES (0, true, 'managerowner', '$2a$04$j/yqCtlHxKmdxHMWxaji4OD1w591LIMNDGBqUbCpD6HTM4aj2uLiS', 'en_US', 'CONFIRMED', 4, now(), now(), false, '59f5bee8-f5bf-11ed-b67e-0242ac120002');
INSERT INTO role (permission_level, active, version, account_id, created_on, updated_on) VALUES ('OWNER', true, 0, 4, now(), now());
INSERT INTO role (permission_level, active, version, account_id, created_on, updated_on) VALUES ('FACILITY_MANAGER', false, 0, 4, now(), now());
INSERT INTO role (permission_level, active, version, account_id, created_on, updated_on) VALUES ('ADMINISTRATOR', false, 0, 4, now(), now());
INSERT INTO owner (id) VALUES (10);
INSERT INTO facility_manager (id) VALUES (11);
INSERT INTO administrator (id) VALUES (12);
INSERT INTO auth_info (last_ip_address, last_success_auth, last_incorrect_auth, incorrect_auth_count, created_on, updated_on, version, account_id) VALUES (null, null, null, 0, now(), now(), 0, 4);
# add test OWNER password jantes123
INSERT INTO account_details (version, email, first_name, last_name, phone_number, created_on, updated_on) VALUES (0, 'nikat2001@gmail.com', 'Weronika', 'Testowa', '123462387', now(), now());
INSERT INTO account (version, active, login, password, locale, account_state, account_details_id, created_on, updated_on, two_factor_enabled, otp_secret) VALUES (0, true, 'nikattoja', '$2a$04$j/yqCtlHxKmdxHMWxaji4OD1w591LIMNDGBqUbCpD6HTM4aj2uLiS', 'en_US', 'CONFIRMED', 5, now(), now(), false, '59f5bee8-f5bf-11ed-b67e-0242ac120002');
INSERT INTO role (permission_level, active, version, account_id, created_on, updated_on) VALUES ('OWNER', true, 0, 5, now(), now());
INSERT INTO role (permission_level, active, version, account_id, created_on, updated_on) VALUES ('FACILITY_MANAGER', false, 0, 5, now(), now());
INSERT INTO role (permission_level, active, version, account_id, created_on, updated_on) VALUES ('ADMINISTRATOR', false, 0, 5, now(), now());
INSERT INTO owner (id) VALUES (13);
INSERT INTO facility_manager (id) VALUES (14);
INSERT INTO administrator (id) VALUES (15);
INSERT INTO auth_info (last_ip_address, last_success_auth, last_incorrect_auth, incorrect_auth_count, created_on, updated_on, version, account_id) VALUES (null, null, null, 0, now(), now(), 0, 5);
# mol
INSERT INTO invoice(version, created_on, created_by, updated_on, updated_by, invoice_number, water_usage, date) values (0, now(), null, now(), null, 'FV 2020/01/23', 29.090, '2023-04-1');
INSERT INTO invoice(version, created_on, created_by, updated_on, updated_by, invoice_number, water_usage, date) values (1, now(), null, now(), null, 'FV 2020/02/23', 30.090, '2023-05-1');
INSERT INTO entity_consistence_assurance (topic, version, created_on, updated_on) VALUES ('TARIFF_PERSISTENCE', 0, now(), now());
INSERT INTO tariff(version, created_on, created_by, updated_on, updated_by, cold_water_price, hot_water_price, trash_price, start_date, end_date, entity_consistence_assurance_id) values (0, now(), null, now(), null, 9.81, 9.02, 2.22, '2023-01-01', '2023-05-31', (SELECT id FROM entity_consistence_assurance WHERE topic = 'TARIFF_PERSISTENCE'));
INSERT INTO tariff(version, created_on, created_by, updated_on, updated_by, cold_water_price, hot_water_price, trash_price, start_date, end_date, entity_consistence_assurance_id) values (0, now(), null, now(), null, 9.81, 9.02, 2.22, '2023-02-01', '2023-03-31', (SELECT id FROM entity_consistence_assurance WHERE topic = 'TARIFF_PERSISTENCE'));
INSERT INTO tariff(version, created_on, created_by, updated_on, updated_by, cold_water_price, hot_water_price, trash_price, start_date, end_date, entity_consistence_assurance_id) values (0, now(), null, now(), null, 9.81, 9.02, 2.22, '2023-06-01', '2024-08-30', (SELECT id FROM entity_consistence_assurance WHERE topic = 'TARIFF_PERSISTENCE'));
INSERT INTO apartment (version, number, created_on, updated_on, area, owner_id) VALUES (0, '12a', now(), now(), 40.00, 2);
INSERT INTO apartment (version, number, created_on, updated_on, area, owner_id) VALUES (0, '11a', now(), now(), 50.00, 1);
INSERT INTO entity_consistence_assurance (topic, version, created_on, updated_on) VALUES ('MAIN_WATER_METER_PERSISTENCE', 0, now(), now());
INSERT INTO water_meter (version, active, serial_number, expiry_date, starting_value, expected_daily_usage, type, apartment_id, created_on, updated_on) VALUES (0, true, '123456789 A', now() + INTERVAL '360 days', 100.000, 0.500, 'HOT_WATER', 1, now(), now());
INSERT INTO water_meter (version, active, serial_number, expiry_date, starting_value, expected_daily_usage, type, apartment_id, created_on, updated_on) VALUES (0, true, '123456789 B', now() + INTERVAL '360 days', 100.000, 0.600, 'COLD_WATER', 1, now(), now());
INSERT INTO water_meter (version, active, serial_number, expiry_date, starting_value, expected_daily_usage, type, apartment_id, created_on, updated_on) VALUES (0, true, '123456789 C', now() + INTERVAL '360 days', 100.000, 0.700, 'COLD_WATER', 1, now(), now());
INSERT INTO water_meter (version, active, serial_number, expiry_date, starting_value, expected_daily_usage, type, apartment_id, created_on, updated_on) VALUES (0, true, '123456789 D', now() + INTERVAL '360 days', 100.000, 0.200, 'HOT_WATER', 2, now(), now());
INSERT INTO water_meter (version, active, serial_number, expiry_date, starting_value, expected_daily_usage, type, apartment_id, created_on, updated_on) VALUES (0, true, '123456789 E', now() + INTERVAL '360 days', 100.000, 0.300, 'COLD_WATER', 2, now(), now());
INSERT INTO water_meter (version, active, serial_number, expiry_date, starting_value, expected_daily_usage, type, apartment_id, entity_consistence_assurance_id, created_on, updated_on) VALUES (0, true, '123456789 F', now() + INTERVAL '360 days', 0.000, 0.000, 'MAIN', null, (SELECT id FROM entity_consistence_assurance WHERE topic = 'MAIN_WATER_METER_PERSISTENCE'), now(), now());
INSERT INTO usage_report (created_on, updated_on, version, cold_water_cost, cold_water__usage, garbage_cost, hot_water_cost, hot_water_usage, unbilled_water_amount, unbilled_water_cost) VALUES (now(), now(), 0, 10, 10, 10, 10, 10, 10, 10);
INSERT INTO usage_report (created_on, updated_on, version, cold_water_cost, cold_water__usage, garbage_cost, hot_water_cost, hot_water_usage, unbilled_water_amount, unbilled_water_cost) VALUES (now(), now(), 0, 11, 12, 13, 14, 15, 16, 17);
INSERT INTO bill (version, created_on, updated_on, balance, date, advance_usage, apartment_id, owner_id, real_usage) VALUES (0, now(), now(), 9.02, '2023-01-01', 1, 1, 2, 2);
INSERT INTO bill (version, created_on, updated_on, balance, date, advance_usage, apartment_id, owner_id, real_usage) VALUES (0, now(), now(), 420.69, '2023-01-01', 1, 2, 1, 2);
INSERT INTO water_usage_stats (created_on, updated_on, version, cold_water_usage, hot_water_usage, date, apartment_id) VALUES (now(), now(), 0, 40.000, 60.000, '2023-03-01', 1);
INSERT INTO water_usage_stats (created_on, updated_on, version, cold_water_usage, hot_water_usage, date, apartment_id) VALUES (now(), now(), 0, 40.000, 60.000, '2023-03-01', 2);