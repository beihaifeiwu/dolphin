-- Sequence: "AdminSequence"

-- DROP SEQUENCE "AdminSequence";

CREATE SEQUENCE "AdminSequence"
INCREMENT 1
MINVALUE 1
MAXVALUE 999999
START 1
CACHE 1;
ALTER TABLE "AdminSequence"
OWNER TO postgres;

-- Table: "Admin"

-- DROP TABLE "Admin";

CREATE TABLE "Admin"
(
  "ID" bigint NOT NULL DEFAULT nextval('"AdminSequence"'::regclass),
  "Name" character varying(20),
  "Password" text,
  "MallId" bigint,
  "CreateTime" timestamp with time zone,
  CONSTRAINT "Admin_pkey" PRIMARY KEY ("ID")
)
WITH (
OIDS=FALSE
);
ALTER TABLE "Admin"
OWNER TO postgres;

-- Sequence: "BeaconSequence"

-- DROP SEQUENCE "BeaconSequence";

CREATE SEQUENCE "BeaconSequence"
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775806
  START 1
  CACHE 1;
ALTER TABLE "BeaconSequence"
  OWNER TO postgres;

-- Table: "Beacon"

-- DROP TABLE "Beacon";

CREATE TABLE "Beacon"
(
  "ID" bigint NOT NULL DEFAULT nextval('"BeaconSequence"'::regclass),
  "MAC" character varying(20),
  "UUID" character varying(50),
  "Major" integer,
  "Minor" integer,
  "MeasuredPower" integer,
  "RSSI" integer,
  "BatteryLevel" integer,
  "Frequency" integer,
  "TxPower" integer,
  "Password" character varying(30),
  "Status" integer,
  "MallId" bigint,
  "FloorId" bigint,
  "Point" geometry(Point,3857),
  "Flag" integer,
  "GatewayId" bigint,
  "ScanTime" timestamp with time zone,
  "CreateTime" timestamp without time zone,
  "Number" character varying(15),
  "Region" bigint,
  "Type" integer,
  CONSTRAINT "Beacon_pkey" PRIMARY KEY ("ID")
)
WITH (
OIDS=FALSE
);
ALTER TABLE "Beacon"
OWNER TO postgres;