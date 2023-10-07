TRUNCATE account, apartment, verification_token, bill, invoice, tariff, usage_report, water_meter, water_meter_check, water_usage_stats,account_details,owner,administrator,facility_manager,role,auth_info,list_search_preferences,two_factor_authentication,entity_consistence_assurance CASCADE;
TRUNCATE TABLE account, apartment, verification_token, bill, invoice, tariff, usage_report, water_meter, water_meter_check, water_usage_stats,account_details,owner,administrator,facility_manager,role,auth_info,list_search_preferences,two_factor_authentication,entity_consistence_assurance RESTART IDENTITY;

GRANT ALL ON SCHEMA public TO ssbd06admin;

GRANT SELECT ON TABLE account TO ssbd06auth;
GRANT SELECT ON TABLE role TO ssbd06auth;
GRANT SELECT ON TABLE auth_info TO ssbd06auth;

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
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE entity_consistence_assurance TO ssbd06mol;

GRANT USAGE, SELECT, UPDATE ON SEQUENCE account_details_id_seq TO ssbd06mok;
GRANT USAGE, SELECT, UPDATE ON SEQUENCE account_id_seq TO ssbd06mok;
GRANT USAGE, SELECT, UPDATE ON SEQUENCE auth_info_id_seq TO ssbd06mok;
GRANT USAGE, SELECT, UPDATE ON SEQUENCE role_id_seq TO ssbd06mok;
GRANT USAGE, SELECT, UPDATE ON SEQUENCE verification_token_id_seq TO ssbd06mok;
GRANT USAGE, SELECT, UPDATE ON SEQUENCE list_search_preferences_id_seq TO ssbd06mok;
GRANT USAGE, SELECT, UPDATE ON SEQUENCE two_factor_authentication_id_seq TO ssbd06mok;
GRANT USAGE, SELECT, UPDATE ON SEQUENCE entity_consistence_assurance_id_seq TO ssbd06mol;

GRANT SELECT ON TABLE account TO ssbd06mol;
GRANT SELECT ON TABLE role TO ssbd06mol;
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

INSERT INTO public.account_details (id, version, email, first_name, last_name, phone_number, created_on, updated_on)
VALUES (nextval('account_details_id_seq'), 0, 'kontomat@gmail.com', 'Mateusz', 'Strzelecki', '123456789', now(), now());
INSERT INTO public.account (id, version, active, login, password, locale, account_state, account_details_id, created_on, updated_on, two_factor_enabled,
                            otp_secret)
VALUES (nextval('account_id_seq'), 0, true, 'admin', '$2a$04$m9vbbL2RTbV/XNC44TEZ0e.t9WH2Q6hjtyEem/siFCdNS564hW68q', 'en_US', 'CONFIRMED', 1, now(), now(),
        false, '610cc564-f5bf-11ed-b67e-0242ac120002');
INSERT INTO public.role (permission_level, id, active, version, account_id, created_on, updated_on)
VALUES ('ADMINISTRATOR', nextval('role_id_seq'), true, 0, 1, now(), now());
INSERT INTO public.role (permission_level, id, active, version, account_id, created_on, updated_on)
VALUES ('FACILITY_MANAGER', nextval('role_id_seq'), false, 0, 1, now(), now());
INSERT INTO public.role (permission_level, id, active, version, account_id, created_on, updated_on)
VALUES ('OWNER', nextval('role_id_seq'), false, 0, 1, now(), now());
INSERT INTO public.administrator (id)
VALUES (1);
INSERT INTO public.facility_manager (id)
VALUES (2);
INSERT INTO public.owner (id)
VALUES (3);
INSERT INTO public.auth_info (id, last_ip_address, last_success_auth, last_incorrect_auth, incorrect_auth_count, created_on, updated_on, version, account_id)
VALUES (nextval('auth_info_id_seq'), null, null, null, 0, now(), now(), 0, 1);
INSERT INTO public.account_details (id, version, email, first_name, last_name, phone_number, created_on, updated_on)
VALUES (nextval('account_details_id_seq'), 0, 'kontosz@gmail.com', 'Szymon', 'Ziemecki', '123412341', now(), now());
INSERT INTO public.account (id, version, active, login, password, locale, account_state, account_details_id, created_on, updated_on, two_factor_enabled,
                            otp_secret)
VALUES (nextval('account_id_seq'), 0, true, 'new', '$2a$04$j/yqCtlHxKmdxHMWxaji4OD1w591LIMNDGBqUbCpD6HTM4aj2uLiS', 'en_US', 'CONFIRMED', 2, now(), now(), false,
        '7a1dbcfc-f5bf-11ed-b67e-0242ac120002');
INSERT INTO public.role (permission_level, id, active, version, account_id, created_on, updated_on)
VALUES ('OWNER', nextval('role_id_seq'), true, 0, 2, now(), now());
INSERT INTO public.role (permission_level, id, active, version, account_id, created_on, updated_on)
VALUES ('FACILITY_MANAGER', nextval('role_id_seq'), false, 0, 2, now(), now());
INSERT INTO public.role (permission_level, id, active, version, account_id, created_on, updated_on)
VALUES ('ADMINISTRATOR', nextval('role_id_seq'), false, 0, 2, now(), now());
INSERT INTO public.owner (id)
VALUES (4);
INSERT INTO public.administrator (id)
VALUES (5);
INSERT INTO public.facility_manager (id)
VALUES (6);
INSERT INTO public.auth_info (id, last_ip_address, last_success_auth, last_incorrect_auth, incorrect_auth_count, created_on, updated_on, version, account_id)
VALUES (nextval('auth_info_id_seq'), null, null, null, 0, now(), now(), 0, 2);
INSERT INTO public.account_details (id, version, email, first_name, last_name, phone_number, created_on, updated_on)
VALUES (nextval('account_details_id_seq'), 0, 'tomdut@gmail.com', 'Tom', 'Dut', '666666666', now(), now());
INSERT INTO public.account (id, version, active, login, password, locale, account_state, account_details_id, created_on, updated_on, two_factor_enabled,
                            otp_secret)
VALUES (nextval('account_id_seq'), 0, true, 'tomdut', '$2a$04$j/yqCtlHxKmdxHMWxaji4OD1w591LIMNDGBqUbCpD6HTM4aj2uLiS', 'en_US', 'CONFIRMED', 3, now(), now(),
        false, '81250e56-f5bf-11ed-b67e-0242ac120002');
INSERT INTO public.role (permission_level, id, active, version, account_id, created_on, updated_on)
VALUES ('FACILITY_MANAGER', nextval('role_id_seq'), true, 0, 3, now(), now());
INSERT INTO public.role (permission_level, id, active, version, account_id, created_on, updated_on)
VALUES ('OWNER', nextval('role_id_seq'), false, 0, 3, now(), now());
INSERT INTO public.role (permission_level, id, active, version, account_id, created_on, updated_on)
VALUES ('ADMINISTRATOR', nextval('role_id_seq'), false, 0, 3, now(), now());
INSERT INTO public.facility_manager (id)
VALUES (7);
INSERT INTO public.owner (id)
VALUES (8);
INSERT INTO public.administrator (id)
VALUES (9);
INSERT INTO public.auth_info (id, last_ip_address, last_success_auth, last_incorrect_auth, incorrect_auth_count, created_on, updated_on, version, account_id)
VALUES (nextval('auth_info_id_seq'), null, null, null, 0, now(), now(), 0, 3);

INSERT INTO public.account_details (id, version, email, first_name, last_name, phone_number, created_on, updated_on)
VALUES (nextval('account_details_id_seq'), 0, 'skulmikpl@gmail.com', 'Piotr', 'Skonieczny', '725510347', now(), now());
INSERT INTO public.account (id, version, active, login, password, locale, account_state, account_details_id, created_on, updated_on, two_factor_enabled,
                            otp_secret)
VALUES (nextval('account_id_seq'), 0, true, 'skulmik', '$2a$04$j/yqCtlHxKmdxHMWxaji4OD1w591LIMNDGBqUbCpD6HTM4aj2uLiS', 'en_US', 'TO_CONFIRM', 4, now(), now(),
        false, '83d7c4cc-f5bf-11ed-b67e-0242ac120002');
INSERT INTO public.role (permission_level, id, active, version, account_id, created_on, updated_on)
VALUES ('OWNER', nextval('role_id_seq'), true, 0, 4, now(), now());
INSERT INTO public.role (permission_level, id, active, version, account_id, created_on, updated_on)
VALUES ('FACILITY_MANAGER', nextval('role_id_seq'), false, 0, 4, now(), now());
INSERT INTO public.role (permission_level, id, active, version, account_id, created_on, updated_on)
VALUES ('ADMINISTRATOR', nextval('role_id_seq'), false, 0, 4, now(), now());
INSERT INTO public.owner (id)
VALUES (10);
INSERT INTO public.facility_manager (id)
VALUES (11);
INSERT INTO public.administrator (id)
VALUES (12);
INSERT INTO public.auth_info (id, last_ip_address, last_success_auth, last_incorrect_auth, incorrect_auth_count, created_on, updated_on, version, account_id)
VALUES (nextval('auth_info_id_seq'), null, null, null, 0, now(), now(), 0, 4);

INSERT INTO public.account_details (id, version, email, first_name, last_name, phone_number, created_on, updated_on)
VALUES (nextval('account_details_id_seq'), 0, 'mol-owner@gmail.com', 'Jerzy', 'Białowieży', '698667546', now(), now());
INSERT INTO public.account (id, version, active, login, password, locale, account_state, account_details_id, created_on, updated_on, two_factor_enabled,
                            otp_secret)
VALUES (nextval('account_id_seq'), 0, true, 'jerzy', '$2a$04$j/yqCtlHxKmdxHMWxaji4OD1w591LIMNDGBqUbCpD6HTM4aj2uLiS', 'en_US', 'CONFIRMED', 5, now(), now(),
        false, '7a1dbcfc-f5bf-11ed-b67e-0242ac120002');
INSERT INTO public.role (permission_level, id, active, version, account_id, created_on, updated_on)
VALUES ('OWNER', nextval('role_id_seq'), true, 0, 5, now(), now());
INSERT INTO public.role (permission_level, id, active, version, account_id, created_on, updated_on)
VALUES ('FACILITY_MANAGER', nextval('role_id_seq'), false, 0, 5, now(), now());
INSERT INTO public.role (permission_level, id, active, version, account_id, created_on, updated_on)
VALUES ('ADMINISTRATOR', nextval('role_id_seq'), false, 0, 5, now(), now());
INSERT INTO public.owner (id)
VALUES (13);
INSERT INTO public.administrator (id)
VALUES (14);
INSERT INTO public.facility_manager (id)
VALUES (15);
INSERT INTO public.auth_info (id, last_ip_address, last_success_auth, last_incorrect_auth, incorrect_auth_count, created_on, updated_on, version, account_id)
VALUES (nextval('auth_info_id_seq'), null, null, null, 0, now(), now(), 0, 5);

INSERT INTO public.apartment (id, version, number, created_on, updated_on, area, owner_id)
VALUES (nextval('apartment_id_seq'), 0, '12a', now(), now(), 40.00, 2);
INSERT INTO public.apartment (id, version, number, created_on, updated_on, area, owner_id)
VALUES (nextval('apartment_id_seq'), 0, '12b', now(), now(), 40.00, 2);
INSERT INTO public.entity_consistence_assurance (id, topic, version, created_on, updated_on)
VALUES (nextval('entity_consistence_assurance_id_seq'), 'MAIN_WATER_METER_PERSISTENCE', 0, now(), now());
INSERT INTO public.entity_consistence_assurance (id, topic, version, created_on, updated_on)
VALUES (nextval('entity_consistence_assurance_id_seq'), 'TARIFF_PERSISTENCE', 0, now(), now());
INSERT INTO public.water_meter (id, version, active, serial_number, expiry_date, starting_value, expected_daily_usage, type, apartment_id, created_on,
                                updated_on)
VALUES (nextval('water_meter_id_seq'), 0, true, '1234567 A', now() + INTERVAL '360 days', 100.000, 500.000, 'HOT_WATER', 1, now(), now());
INSERT INTO public.water_meter (id, version, active, serial_number, expiry_date, starting_value, expected_daily_usage, type, apartment_id, created_on,
                                updated_on)
VALUES (nextval('water_meter_id_seq'), 0, true, '1234567 B', now() + INTERVAL '360 days', 100.000, 50.000, 'COLD_WATER', 1, now(), now());
INSERT INTO public.water_meter (id, version, active, serial_number, expiry_date, starting_value, expected_daily_usage, type, apartment_id,
                                entity_consistence_assurance_id, created_on, updated_on)
VALUES (nextval('water_meter_id_seq'), 0, true, '123456789 C', now() + INTERVAL '360 days', 0.000, 0.000, 'MAIN', null,
        (SELECT id FROM public.entity_consistence_assurance WHERE topic = 'MAIN_WATER_METER_PERSISTENCE'), now(), now());
INSERT INTO public.usage_report (id, created_on, updated_on, version, cold_water_cost, cold_water__usage, garbage_cost, hot_water_cost, hot_water_usage,
                                 unbilled_water_amount, unbilled_water_cost)
VALUES (nextval('usage_report_id_seq'), now(), now(), 0, 10, 10, 10, 10, 10, 10, 10);
INSERT INTO public.usage_report (id, created_on, updated_on, version, cold_water_cost, cold_water__usage, garbage_cost, hot_water_cost, hot_water_usage,
                                 unbilled_water_amount, unbilled_water_cost)
VALUES (nextval('usage_report_id_seq'), now(), now(), 0, 11, 12, 13, 14, 15, 16, 17);
INSERT INTO public.bill (id, version, created_on, updated_on, balance, date, advance_usage, apartment_id, owner_id, real_usage)
VALUES (nextval('bill_id_seq'), 0, now(), now(), 9.02, now(), 1, 1, 2, 2);
INSERT INTO public.water_usage_stats (id, created_on, updated_on, version, cold_water_usage, hot_water_usage, year_month, apartment_id)
VALUES (nextval('water_usage_stats_id_seq'), now(), now(), 0, 40.000, 60.000, '2023-06-01', 1);
INSERT INTO public.water_usage_stats (id, created_on, updated_on, version, cold_water_usage, hot_water_usage, year_month, apartment_id)
VALUES (nextval('water_usage_stats_id_seq'), now(), now(), 0, 40.000, 60.000, '2023-09-01', 1);
INSERT INTO public.water_usage_stats (id, created_on, updated_on, version, cold_water_usage, hot_water_usage, year_month, apartment_id)
VALUES (nextval('water_usage_stats_id_seq'), now(), now(), 0, 40.000, 60.000, '2023-10-01', 1);
INSERT INTO public.water_usage_stats (id, created_on, updated_on, version, cold_water_usage, hot_water_usage, year_month, apartment_id)
VALUES (nextval('water_usage_stats_id_seq'), now(), now(), 0, 40.000, 60.000, '2023-09-01', 2);
INSERT INTO public.water_usage_stats (id, created_on, updated_on, version, cold_water_usage, hot_water_usage, year_month, apartment_id)
VALUES (nextval('water_usage_stats_id_seq'), now(), now(), 0, 40.000, 60.000, '2023-10-01', 2);
INSERT INTO public.tariff(id, version, created_on, created_by, updated_on, updated_by, cold_water_price, hot_water_price, trash_price, start_date, end_date, entity_consistence_assurance_id)
values (nextval('tariff_id_seq'), 0, now(), null, now(), null, 9.81, 9.02, 2.22, '2023-05-01', '2023-11-30', (SELECT id FROM public.entity_consistence_assurance WHERE topic = 'MAIN_WATER_METER_PERSISTENCE'));
INSERT INTO public.water_meter_check(id, version, created_on, created_by, updated_on, updated_by, meter_reading, check_date, manager_authored, water_meter_id)
values (nextval('water_meter_check_id_seq'), 0, now(), null, now(), null, 120.000, '2023-09-16', true, 1);
INSERT INTO public.water_meter_check(id, version, created_on, created_by, updated_on, updated_by, meter_reading, check_date, manager_authored, water_meter_id)
values (nextval('water_meter_check_id_seq'), 0, now(), null, now(), null, 120.000, '2023-09-16', true, 2);

INSERT INTO public.water_meter_check(id, version, created_on, created_by, updated_on, updated_by, meter_reading, check_date, manager_authored, water_meter_id)
values (nextval('water_meter_check_id_seq'), 0, now(), null, now(), null, 120.000, '2031-11-16', true, 1);
INSERT INTO public.water_meter_check(id, version, created_on, created_by, updated_on, updated_by, meter_reading, check_date, manager_authored, water_meter_id)
values (nextval('water_meter_check_id_seq'), 0, now(), null, now(), null, 120.000, '2031-11-16', true, 2);
INSERT INTO public.water_meter_check(id, version, created_on, created_by, updated_on, updated_by, meter_reading, check_date, manager_authored, water_meter_id)
values (nextval('water_meter_check_id_seq'), 0, now(), null, now(), null, 120.000, '2031-11-16', true, 3);
INSERT INTO public.tariff(id, version, created_on, created_by, updated_on, updated_by, cold_water_price, hot_water_price, trash_price, start_date, end_date,
                          entity_consistence_assurance_id)
values (nextval('tariff_id_seq'), 0, now(), null, now(), null, 9.81, 9.02, 2.22, '2031-07-01', '2031-08-30',
        (SELECT id FROM public.entity_consistence_assurance WHERE topic = 'MAIN_WATER_METER_PERSISTENCE'));

INSERT INTO public.tariff(id, version, created_on, created_by, updated_on, updated_by, cold_water_price, hot_water_price, trash_price, start_date, end_date,
                          entity_consistence_assurance_id)
values (nextval('tariff_id_seq'), 0, now(), null, now(), null, 9.81, 9.02, 2.22, '2031-04-01', '2031-06-30',
        (SELECT id FROM public.entity_consistence_assurance WHERE topic = 'MAIN_WATER_METER_PERSISTENCE'));
INSERT INTO public.bill(id, created_on, updated_on, version, date, balance, apartment_id, owner_id, advance_usage, real_usage)
VALUES (nextval('bill_id_seq'), now(), now(), 0, '2023-09-01', 1000.00, 1, 2, 1, 2);
INSERT INTO public.bill(id, created_on, updated_on, version, date, balance, apartment_id, owner_id, advance_usage, real_usage)
VALUES (nextval('bill_id_seq'), now(), now(), 0, '2023-09-01', 1000.00, 2, 2, 1, 2);