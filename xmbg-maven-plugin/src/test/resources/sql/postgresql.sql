-- Sequence: "task_sequence"

-- DROP SEQUENCE "task_sequence";

CREATE SEQUENCE "task_sequence"
INCREMENT 1
MINVALUE 1
MAXVALUE 999999
START 1
CACHE 1;
ALTER TABLE "task_sequence"
OWNER TO root;

-- Table: "task"

-- DROP TABLE "task";

CREATE TABLE "task"
(
  "id" bigint NOT NULL DEFAULT nextval('"task_sequence"'::regclass),
  "title" character varying(20) NOT NULL,
  "description" text,
  "user_id" bigint NOT NULL,
  CONSTRAINT "task_pkey" PRIMARY KEY ("id")
)
WITH (
OIDS=FALSE
);
ALTER TABLE "task"
OWNER TO root;

-- Sequence: "user_sequence"

-- DROP SEQUENCE "user_sequence";

CREATE SEQUENCE "user_sequence"
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775806
  START 1
  CACHE 1;
ALTER TABLE "user_sequence"
  OWNER TO root;

-- Table: "user"

-- DROP TABLE "user";

CREATE TABLE "user"
(
  "id" bigint NOT NULL DEFAULT nextval('"user_sequence"'::regclass),
  "login_name" character varying(64) NOT NULL UNIQUE,
  "name" character varying(50) NOT NULL,
  "password" character varying(255) NOT NULL,
  "salt" character varying(255) NOT NULL,
  "roles" character varying(255) NULL,
  "register_date" timestamp without time zone DEFAULT now(),
  CONSTRAINT "user_pkey" PRIMARY KEY ("id")
)
WITH (
OIDS=FALSE
);
ALTER TABLE "user"
OWNER TO root;