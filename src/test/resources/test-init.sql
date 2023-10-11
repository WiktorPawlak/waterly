DELETE
FROM account;
DELETE
FROM apartment;
DELETE
FROM verification_token;
DELETE
FROM bill;
DELETE
FROM invoice;
DELETE
FROM tariff;
DELETE
FROM usage_report;
DELETE
FROM water_meter;
DELETE
FROM water_meter_check;
DELETE
FROM water_usage_stats;
DELETE
FROM account_details;
DELETE
FROM owner;
DELETE
FROM administrator;
DELETE
FROM facility_manager;
DELETE
FROM role;
DELETE
FROM auth_info;
DELETE
FROM list_search_preferences;
DELETE
FROM two_factor_authentication;
DELETE
FROM entity_consistence_assurance;

GRANT ALL PRIVILEGES ON ssbd06.* TO ssbd06admin;

GRANT SELECT ON account TO ssbd06auth;
GRANT SELECT ON role TO ssbd06auth;
GRANT SELECT ON auth_info TO ssbd06auth;

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
GRANT SELECT, INSERT, UPDATE, DELETE ON entity_consistence_assurance TO ssbd06mol;

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

INSERT INTO account_details (version, email, first_name, last_name, phone_number, created_on, updated_on)
VALUES (0, 'kontomat@gmail.com', 'Mateusz', 'Strzelecki', '123456789', NOW(), NOW());
INSERT INTO account (version, active, login, password, locale, account_state, account_details_id, created_on, updated_on, two_factor_enabled,
                     otp_secret)
VALUES (0, true, 'admin', '$2a$04$m9vbbL2RTbV/XNC44TEZ0e.t9WH2Q6hjtyEem/siFCdNS564hW68q', 'en_US', 'CONFIRMED', 1, NOW(), NOW(),
        false, '610cc564-f5bf-11ed-b67e-0242ac120002');
INSERT INTO role (permission_level, id, active, version, account_id, created_on, updated_on)
VALUES ('ADMINISTRATOR', true, 0, 1, NOW(), NOW());
INSERT INTO role (permission_level, id, active, version, account_id, created_on, updated_on)
VALUES ('FACILITY_MANAGER', false, 0, 1, NOW(), NOW());
INSERT INTO role (permission_level, id, active, version, account_id, created_on, updated_on)
VALUES ('OWNER', false, 0, 1, NOW(), NOW());
INSERT INTO administrator (id)
VALUES (1);
INSERT INTO facility_manager (id)
VALUES (2);
INSERT INTO owner (id)
VALUES (3);
INSERT INTO auth_info (last_ip_address, last_success_auth, last_incorrect_auth, incorrect_auth_count, created_on, updated_on, version, account_id)
VALUES (null, null, null, 0, NOW(), NOW(), 0, 1);
INSERT INTO account_details (version, email, first_name, last_name, phone_number, created_on, updated_on)
VALUES (0, 'kontosz@gmail.com', 'Szymon', 'Ziemecki', '123412341', NOW(), NOW());
INSERT INTO account (version, active, login, password, locale, account_state, account_details_id, created_on, updated_on, two_factor_enabled,
                     otp_secret)
VALUES (0, true, 'new', '$2a$04$j/yqCtlHxKmdxHMWxaji4OD1w591LIMNDGBqUbCpD6HTM4aj2uLiS', 'en_US', 'CONFIRMED', 2, NOW(), NOW(), false,
        '7a1dbcfc-f5bf-11ed-b67e-0242ac120002');
INSERT INTO role (permission_level, id, active, version, account_id, created_on, updated_on)
VALUES ('OWNER', true, 0, 2, NOW(), NOW());
INSERT INTO role (permission_level, id, active, version, account_id, created_on, updated_on)
VALUES ('FACILITY_MANAGER', false, 0, 2, NOW(), NOW());
INSERT INTO role (permission_level, id, active, version, account_id, created_on, updated_on)
VALUES ('ADMINISTRATOR', false, 0, 2, NOW(), NOW());
INSERT INTO owner (id)
VALUES (4);
INSERT INTO administrator (id)
VALUES (5);
INSERT INTO facility_manager (id)
VALUES (6);
INSERT INTO auth_info (last_ip_address, last_success_auth, last_incorrect_auth, incorrect_auth_count, created_on, updated_on, version, account_id)
VALUES (null, null, null, 0, NOW(), NOW(), 0, 2);
INSERT INTO account_details (version, email, first_name, last_name, phone_number, created_on, updated_on)
VALUES (0, 'tomdut@gmail.com', 'Tom', 'Dut', '666666666', NOW(), NOW());
INSERT INTO account (version, active, login, password, locale, account_state, account_details_id, created_on, updated_on, two_factor_enabled,
                     otp_secret)
VALUES (0, true, 'tomdut', '$2a$04$j/yqCtlHxKmdxHMWxaji4OD1w591LIMNDGBqUbCpD6HTM4aj2uLiS', 'en_US', 'CONFIRMED', 3, NOW(), NOW(),
        false, '81250e56-f5bf-11ed-b67e-0242ac120002');
INSERT INTO role (permission_level, id, active, version, account_id, created_on, updated_on)
VALUES ('FACILITY_MANAGER', true, 0, 3, NOW(), NOW());
INSERT INTO role (permission_level, id, active, version, account_id, created_on, updated_on)
VALUES ('OWNER', false, 0, 3, NOW(), NOW());
INSERT INTO role (permission_level, id, active, version, account_id, created_on, updated_on)
VALUES ('ADMINISTRATOR', false, 0, 3, NOW(), NOW());
INSERT INTO facility_manager (id)
VALUES (7);
INSERT INTO owner (id)
VALUES (8);
INSERT INTO administrator (id)
VALUES (9);
INSERT INTO auth_info (last_ip_address, last_success_auth, last_incorrect_auth, incorrect_auth_count, created_on, updated_on, version, account_id)
VALUES (null, null, null, 0, NOW(), NOW(), 0, 3);

INSERT INTO account_details (version, email, first_name, last_name, phone_number, created_on, updated_on)
VALUES (0, 'skulmikpl@gmail.com', 'Piotr', 'Skonieczny', '725510347', NOW(), NOW());
INSERT INTO account (version, active, login, password, locale, account_state, account_details_id, created_on, updated_on, two_factor_enabled,
                     otp_secret)
VALUES (0, true, 'skulmik', '$2a$04$j/yqCtlHxKmdxHMWxaji4OD1w591LIMNDGBqUbCpD6HTM4aj2uLiS', 'en_US', 'TO_CONFIRM', 4, NOW(), NOW(),
        false, '83d7c4cc-f5bf-11ed-b67e-0242ac120002');
INSERT INTO role (permission_level, id, active, version, account_id, created_on, updated_on)
VALUES ('OWNER', true, 0, 4, NOW(), NOW());
INSERT INTO role (permission_level, id, active, version, account_id, created_on, updated_on)
VALUES ('FACILITY_MANAGER', false, 0, 4, NOW(), NOW());
INSERT INTO role (permission_level, id, active, version, account_id, created_on, updated_on)
VALUES ('ADMINISTRATOR', false, 0, 4, NOW(), NOW());
INSERT INTO owner (id)
VALUES (10);
INSERT INTO facility_manager (id)
VALUES (11);
INSERT INTO administrator (id)
VALUES (12);
INSERT INTO auth_info (last_ip_address, last_success_auth, last_incorrect_auth, incorrect_auth_count, created_on, updated_on, version, account_id)
VALUES (null, null, null, 0, NOW(), NOW(), 0, 4);

INSERT INTO account_details (version, email, first_name, last_name, phone_number, created_on, updated_on)
VALUES (0, 'mol-owner@gmail.com', 'Jerzy', 'Białowieży', '698667546', NOW(), NOW());
INSERT INTO account (version, active, login, password, locale, account_state, account_details_id, created_on, updated_on, two_factor_enabled,
                     otp_secret)
VALUES (0, true, 'jerzy', '$2a$04$j/yqCtlHxKmdxHMWxaji4OD1w591LIMNDGBqUbCpD6HTM4aj2uLiS', 'en_US', 'CONFIRMED', 5, NOW(), NOW(),
        false, '7a1dbcfc-f5bf-11ed-b67e-0242ac120002');
INSERT INTO role (permission_level, id, active, version, account_id, created_on, updated_on)
VALUES ('OWNER', true, 0, 5, NOW(), NOW());
INSERT INTO role (permission_level, id, active, version, account_id, created_on, updated_on)
VALUES ('FACILITY_MANAGER', false, 0, 5, NOW(), NOW());
INSERT INTO role (permission_level, id, active, version, account_id, created_on, updated_on)
VALUES ('ADMINISTRATOR', false, 0, 5, NOW(), NOW());
INSERT INTO owner (id)
VALUES (13);
INSERT INTO administrator (id)
VALUES (14);
INSERT INTO facility_manager (id)
VALUES (15);
INSERT INTO auth_info (last_ip_address, last_success_auth, last_incorrect_auth, incorrect_auth_count, created_on, updated_on, version, account_id)
VALUES (null, null, null, 0, NOW(), NOW(), 0, 5);

INSERT INTO apartment (version, number, created_on, updated_on, area, owner_id)
VALUES (0, '12a', NOW(), NOW(), 40.00, 2);
INSERT INTO apartment (version, number, created_on, updated_on, area, owner_id)
VALUES (0, '12b', NOW(), NOW(), 40.00, 2);
INSERT INTO entity_consistence_assurance (topic, version, created_on, updated_on)
VALUES ('MAIN_WATER_METER_PERSISTENCE', 0, NOW(), NOW());
INSERT INTO entity_consistence_assurance (topic, version, created_on, updated_on)
VALUES ('TARIFF_PERSISTENCE', 0, NOW(), NOW());
INSERT INTO water_meter (version, active, serial_number, expiry_date, starting_value, expected_daily_usage, type, apartment_id, created_on,
                         updated_on)
VALUES (0, true, '1234567 A', NOW() + INTERVAL 360 day, 100.000, 500.000, 'HOT_WATER', 1, NOW(), NOW());
INSERT INTO water_meter (version, active, serial_number, expiry_date, starting_value, expected_daily_usage, type, apartment_id, created_on,
                         updated_on)
VALUES (0, true, '1234567 B', NOW() + INTERVAL 360 day, 100.000, 50.000, 'COLD_WATER', 1, NOW(), NOW());
INSERT INTO water_meter (version, active, serial_number, expiry_date, starting_value, expected_daily_usage, type, apartment_id,
                         entity_consistence_assurance_id, created_on, updated_on)
VALUES (0, true, '123456789 C', NOW() + INTERVAL 360 day, 0.000, 0.000, 'MAIN', null,
        (SELECT id FROM entity_consistence_assurance WHERE topic = 'MAIN_WATER_METER_PERSISTENCE'), NOW(), NOW());
INSERT INTO usage_report (created_on, updated_on, version, cold_water_cost, cold_water__usage, garbage_cost, hot_water_cost, hot_water_usage,
                          unbilled_water_amount, unbilled_water_cost)
VALUES (NOW(), NOW(), 0, 10, 10, 10, 10, 10, 10, 10);
INSERT INTO usage_report (created_on, updated_on, version, cold_water_cost, cold_water__usage, garbage_cost, hot_water_cost, hot_water_usage,
                          unbilled_water_amount, unbilled_water_cost)
VALUES (NOW(), NOW(), 0, 11, 12, 13, 14, 15, 16, 17);
INSERT INTO bill (version, created_on, updated_on, balance, date, advance_usage, apartment_id, owner_id, real_usage)
VALUES (0, NOW(), NOW(), 9.02, NOW(), 1, 1, 2, 2);
INSERT INTO water_usage_stats (created_on, updated_on, version, cold_water_usage, hot_water_usage, date, apartment_id)
VALUES (NOW(), NOW(), 0, 40.000, 60.000, '2023-06-01', 1);
INSERT INTO water_usage_stats (created_on, updated_on, version, cold_water_usage, hot_water_usage, date, apartment_id)
VALUES (NOW(), NOW(), 0, 40.000, 60.000, '2023-09-01', 1);
INSERT INTO water_usage_stats (created_on, updated_on, version, cold_water_usage, hot_water_usage, date, apartment_id)
VALUES (NOW(), NOW(), 0, 40.000, 60.000, '2023-10-01', 1);
INSERT INTO water_usage_stats (created_on, updated_on, version, cold_water_usage, hot_water_usage, date, apartment_id)
VALUES (NOW(), NOW(), 0, 40.000, 60.000, '2023-09-01', 2);
INSERT INTO water_usage_stats (created_on, updated_on, version, cold_water_usage, hot_water_usage, date, apartment_id)
VALUES (NOW(), NOW(), 0, 40.000, 60.000, '2023-10-01', 2);
INSERT INTO tariff(version, created_on, created_by, updated_on, updated_by, cold_water_price, hot_water_price, trash_price, start_date, end_date,
                   entity_consistence_assurance_id)
values (0, NOW(), null, NOW(), null, 9.81, 9.02, 2.22, '2023-05-01', '2023-11-30',
        (SELECT id FROM entity_consistence_assurance WHERE topic = 'MAIN_WATER_METER_PERSISTENCE'));
INSERT INTO water_meter_check(version, created_on, created_by, updated_on, updated_by, meter_reading, check_date, manager_authored, water_meter_id)
values (0, NOW(), null, NOW(), null, 120.000, '2023-09-16', true, 1);
INSERT INTO water_meter_check(version, created_on, created_by, updated_on, updated_by, meter_reading, check_date, manager_authored, water_meter_id)
values (0, NOW(), null, NOW(), null, 120.000, '2023-09-16', true, 2);

INSERT INTO water_meter_check(version, created_on, created_by, updated_on, updated_by, meter_reading, check_date, manager_authored, water_meter_id)
values (0, NOW(), null, NOW(), null, 120.000, '2031-11-16', true, 1);
INSERT INTO water_meter_check(version, created_on, created_by, updated_on, updated_by, meter_reading, check_date, manager_authored, water_meter_id)
values (0, NOW(), null, NOW(), null, 120.000, '2031-11-16', true, 2);
INSERT INTO water_meter_check(version, created_on, created_by, updated_on, updated_by, meter_reading, check_date, manager_authored, water_meter_id)
values (0, NOW(), null, NOW(), null, 120.000, '2031-11-16', true, 3);
INSERT INTO tariff(version, created_on, created_by, updated_on, updated_by, cold_water_price, hot_water_price, trash_price, start_date, end_date,
                   entity_consistence_assurance_id)
values (0, NOW(), null, NOW(), null, 9.81, 9.02, 2.22, '2031-07-01', '2031-08-30',
        (SELECT id FROM entity_consistence_assurance WHERE topic = 'MAIN_WATER_METER_PERSISTENCE'));

INSERT INTO tariff(version, created_on, created_by, updated_on, updated_by, cold_water_price, hot_water_price, trash_price, start_date, end_date,
                   entity_consistence_assurance_id)
values (0, NOW(), null, NOW(), null, 9.81, 9.02, 2.22, '2031-04-01', '2031-06-30',
        (SELECT id FROM entity_consistence_assurance WHERE topic = 'MAIN_WATER_METER_PERSISTENCE'));
INSERT INTO usage_report (created_on, updated_on, version, cold_water_cost, cold_water__usage, garbage_cost, hot_water_cost, hot_water_usage,
                          unbilled_water_amount, unbilled_water_cost)
VALUES (NOW(), NOW(), 0, 10, 10, 10, 10, 10, 10, 10);

INSERT INTO usage_report (created_on, updated_on, version, cold_water_cost, cold_water__usage, garbage_cost, hot_water_cost, hot_water_usage,
                          unbilled_water_amount, unbilled_water_cost)
VALUES (NOW(), NOW(), 0, 10, 10, 10, 10, 10, 10, 10);
INSERT INTO usage_report (created_on, updated_on, version, cold_water_cost, cold_water__usage, garbage_cost, hot_water_cost, hot_water_usage,
                          unbilled_water_amount, unbilled_water_cost)
VALUES (NOW(), NOW(), 0, 10, 10, 10, 10, 10, 10, 10);

INSERT INTO usage_report (created_on, updated_on, version, cold_water_cost, cold_water__usage, garbage_cost, hot_water_cost, hot_water_usage,
                          unbilled_water_amount, unbilled_water_cost)
VALUES (NOW(), NOW(), 0, 10, 10, 10, 10, 10, 10, 10);

INSERT INTO bill(created_on, updated_on, version, date, balance, apartment_id, owner_id, advance_usage, real_usage)
VALUES (NOW(), NOW(), 0, '2023-09-01', 1000.00, 1, 2, 2, 3);
INSERT INTO bill(created_on, updated_on, version, date, balance, apartment_id, owner_id, advance_usage, real_usage)
VALUES (NOW(), NOW(), 0, '2023-09-01', 1000.00, 2, 2, 4, 5);