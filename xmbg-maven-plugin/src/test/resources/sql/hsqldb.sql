DROP TABLE "task"
IF EXISTS;
CREATE TABLE "task"
(
  "id"          BIGINT PRIMARY KEY NOT NULL IDENTITY,
  "title"       VARCHAR(128)       NOT NULL,
  "description" VARCHAR(255) DEFAULT NULL,
  "user_id"     BIGINT             NOT NULL
);

DROP TABLE "user"
IF EXISTS;
CREATE TABLE "user"
(
  "id"            BIGINT PRIMARY KEY    NOT NULL IDENTITY,
  "login_name"    VARCHAR(64)           NOT NULL,
  "name"          VARCHAR(64)           NOT NULL,
  "password"      VARCHAR(255)          NOT NULL,
  "salt"          VARCHAR(64)           NOT NULL,
  "roles"         VARCHAR(255) DEFAULT NULL,
  "register_date" TIMESTAMP DEFAULT NOW NOT NULL
);
ALTER TABLE PUBLIC."user" ADD CONSTRAINT UNIQUE_LOGIN_NAME UNIQUE ("login_name");