--
-- PostgreSQL database dump
--

-- Dumped from database version 9.3.4
-- Dumped by pg_dump version 9.3.4
-- Started on 2015-02-03 16:04:20

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

SET search_path = public, pg_catalog;

--
-- TOC entry 2094 (class 0 OID 21827)
-- Dependencies: 171
-- Data for Name: Admin; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO "Admin" ("ID", "Name", "Password", "MallId", "CreateTime") VALUES (1, 'admin', '40bd001563085fc35165329ea1ff5c5ecbdbbeef', 44, '2014-07-18 10:38:14.317+08');
INSERT INTO "Admin" ("ID", "Name", "Password", "MallId", "CreateTime") VALUES (2, 'lp', '40bd001563085fc35165329ea1ff5c5ecbdbbeef', 36, '2014-07-28 17:50:02.569+08');


-- Completed on 2015-02-03 16:04:20

--
-- PostgreSQL database dump complete
--

--
-- PostgreSQL database dump
--

-- Dumped from database version 9.3.4
-- Dumped by pg_dump version 9.3.4
-- Started on 2015-02-03 16:00:09

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

SET search_path = public, pg_catalog;

--
-- TOC entry 2094 (class 0 OID 23145)
-- Dependencies: 188
-- Data for Name: Beacon; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO "Beacon" ("ID", "MAC", "UUID", "Major", "Minor", "MeasuredPower", "RSSI", "BatteryLevel", "Frequency", "TxPower", "Password", "Status", "MallId", "FloorId", "Point", "Flag", "GatewayId", "ScanTime", "CreateTime", "Number", "Region", "Type") VALUES (25, NULL, 'E235D2AF240CB72BAD5F1A9117D96C20', 2048, 107, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 36, 3, '0101000020110F000083B9F3D66EC76941FC312D0448044C41', NULL, NULL, NULL, NULL, NULL, NULL, 1);
INSERT INTO "Beacon" ("ID", "MAC", "UUID", "Major", "Minor", "MeasuredPower", "RSSI", "BatteryLevel", "Frequency", "TxPower", "Password", "Status", "MallId", "FloorId", "Point", "Flag", "GatewayId", "ScanTime", "CreateTime", "Number", "Region", "Type") VALUES (17, NULL, 'E235D2AF240CB72BAD5F1A9117D96C20', 2048, 103, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 36, 3, '0101000020110F0000C2E7088E6EC76941CDC00EE049044C41', NULL, NULL, NULL, NULL, NULL, NULL, 1);
INSERT INTO "Beacon" ("ID", "MAC", "UUID", "Major", "Minor", "MeasuredPower", "RSSI", "BatteryLevel", "Frequency", "TxPower", "Password", "Status", "MallId", "FloorId", "Point", "Flag", "GatewayId", "ScanTime", "CreateTime", "Number", "Region", "Type") VALUES (23, NULL, 'E235D2AF240CB72BAD5F1A9117D96C20', 2048, 106, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 36, 3, '0101000020110F00000C8C69B46EC76941A3C87CAD45044C41', NULL, NULL, NULL, NULL, NULL, NULL, 1);
INSERT INTO "Beacon" ("ID", "MAC", "UUID", "Major", "Minor", "MeasuredPower", "RSSI", "BatteryLevel", "Frequency", "TxPower", "Password", "Status", "MallId", "FloorId", "Point", "Flag", "GatewayId", "ScanTime", "CreateTime", "Number", "Region", "Type") VALUES (22, NULL, 'E235D2AF240CB72BAD5F1A9117D96C20', 2048, 102, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 36, 3, '0101000020110F000038E02FF16EC7694112F0DB4C4C044C41', NULL, NULL, NULL, NULL, NULL, NULL, 2);
INSERT INTO "Beacon" ("ID", "MAC", "UUID", "Major", "Minor", "MeasuredPower", "RSSI", "BatteryLevel", "Frequency", "TxPower", "Password", "Status", "MallId", "FloorId", "Point", "Flag", "GatewayId", "ScanTime", "CreateTime", "Number", "Region", "Type") VALUES (19, NULL, 'E235D2AF240CB72BAD5F1A9117D96C20', 2048, 105, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 36, 3, '0101000020110F00009B559F3770C76941D200DE924E044C41', NULL, NULL, NULL, NULL, NULL, NULL, 1);
INSERT INTO "Beacon" ("ID", "MAC", "UUID", "Major", "Minor", "MeasuredPower", "RSSI", "BatteryLevel", "Frequency", "TxPower", "Password", "Status", "MallId", "FloorId", "Point", "Flag", "GatewayId", "ScanTime", "CreateTime", "Number", "Region", "Type") VALUES (20, NULL, 'E235D2AF240CB72BAD5F1A9117D96C20', 2048, 100, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 36, 3, '0101000020110F00009B559F3770C76941ABCFD5564B044C41', NULL, NULL, NULL, NULL, NULL, NULL, 2);
INSERT INTO "Beacon" ("ID", "MAC", "UUID", "Major", "Minor", "MeasuredPower", "RSSI", "BatteryLevel", "Frequency", "TxPower", "Password", "Status", "MallId", "FloorId", "Point", "Flag", "GatewayId", "ScanTime", "CreateTime", "Number", "Region", "Type") VALUES (24, NULL, 'E235D2AF240CB72BAD5F1A9117D96C20', 2048, 108, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 36, 3, '0101000020110F000090A0F83F6FC769413255303249044C41', NULL, NULL, NULL, NULL, NULL, NULL, 2);
INSERT INTO "Beacon" ("ID", "MAC", "UUID", "Major", "Minor", "MeasuredPower", "RSSI", "BatteryLevel", "Frequency", "TxPower", "Password", "Status", "MallId", "FloorId", "Point", "Flag", "GatewayId", "ScanTime", "CreateTime", "Number", "Region", "Type") VALUES (21, NULL, 'E235D2AF240CB72BAD5F1A9117D96C20', 2048, 101, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 36, 3, '0101000020110F0000F6E39A116FC769412D8BE7D94E044C41', NULL, NULL, NULL, NULL, NULL, NULL, 1);


-- Completed on 2015-02-03 16:00:10

--
-- PostgreSQL database dump complete
--
